package com.soundflow.app.data.remote.soundcloud

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SoundCloudClientIdProvider @Inject constructor(
    @ApplicationContext private val context: Context,
    private val okHttpClient: OkHttpClient
) {
    private val _clientId = MutableStateFlow<String?>(null)
    val clientId: StateFlow<String?> = _clientId.asStateFlow()

    private var lastFetchTime: Long = 0
    private val refreshIntervalMs: Long = 3600_000L

    suspend fun getClientId(): String? {
        if (_clientId.value != null && (System.currentTimeMillis() - lastFetchTime) < refreshIntervalMs) {
            return _clientId.value
        }
        return refreshClientId()
    }

    suspend fun refreshClientId(): String? = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url("https://m.soundcloud.com")
                .header("User-Agent", "Mozilla/5.0 (Linux; Android 14) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36")
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                .build()

            val response = okHttpClient.newCall(request).execute()
            if (!response.isSuccessful) {
                return@withContext _clientId.value
            }

            val html = response.body?.string() ?: return@withContext _clientId.value

            val patterns = listOf(
                Regex("""client_id["\s:=]+["']([a-zA-Z0-9]{32})["']"""),
                Regex("""clientId["\s:=]+["']([a-zA-Z0-9]{32})["']"""),
                Regex(""""client_id":"([a-zA-Z0-9]{32})""""),
                Regex("""client_id=([a-zA-Z0-9]{32})&""")
            )

            for (pattern in patterns) {
                val match = pattern.find(html)
                if (match != null) {
                    val id = match.groupValues[1]
                    _clientId.value = id
                    lastFetchTime = System.currentTimeMillis()
                    return@withContext id
                }
            }

            val scriptUrls = Regex("""src=["'](/assets/[^"']+\.js)["']""").findAll(html).map { it.groupValues[1] }.toList()

            for (scriptUrl in scriptUrls) {
                val fullUrl = if (scriptUrl.startsWith("http")) scriptUrl else "https://m.soundcloud.com$scriptUrl"
                val jsRequest = Request.Builder()
                    .url(fullUrl)
                    .header("User-Agent", "Mozilla/5.0 (Linux; Android 14) AppleWebKit/537.36")
                    .build()

                val jsResponse = okHttpClient.newCall(jsRequest).execute()
                if (!jsResponse.isSuccessful) continue

                val jsContent = jsResponse.body?.string() ?: continue

                for (pattern in patterns) {
                    val match = pattern.find(jsContent)
                    if (match != null) {
                        val id = match.groupValues[1]
                        _clientId.value = id
                        lastFetchTime = System.currentTimeMillis()
                        return@withContext id
                    }
                }
            }

            _clientId.value
        } catch (e: Exception) {
            _clientId.value
        }
    }
}
