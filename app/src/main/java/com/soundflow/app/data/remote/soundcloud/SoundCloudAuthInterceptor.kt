package com.soundflow.app.data.remote.soundcloud

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class SoundCloudAuthInterceptor @Inject constructor(
    private val clientIdProvider: SoundCloudClientIdProvider
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val originalUrl = originalRequest.url

        if (!originalUrl.toString().contains("soundcloud.com")) {
            return chain.proceed(originalRequest)
        }

        if (originalUrl.queryParameter("client_id") != null) {
            return chain.proceed(originalRequest)
        }

        val clientId = runCatching {
            kotlinx.coroutines.runBlocking(kotlinx.coroutines.Dispatchers.IO) {
                clientIdProvider.getClientId()
            }
        }.getOrNull()

        if (clientId == null) {
            return chain.proceed(originalRequest)
        }

        val newUrl = originalUrl.newBuilder()
            .addQueryParameter("client_id", clientId)
            .build()

        val newRequest = originalRequest.newBuilder()
            .url(newUrl)
            .build()

        val response = chain.proceed(newRequest)

        if (response.code == 401 || response.code == 403) {
            response.close()
            runCatching {
                kotlinx.coroutines.runBlocking(kotlinx.coroutines.Dispatchers.IO) {
                    clientIdProvider.refreshClientId()
                }
            }
            val refreshedId = runCatching {
                kotlinx.coroutines.runBlocking(kotlinx.coroutines.Dispatchers.IO) {
                    clientIdProvider.getClientId()
                }
            }.getOrNull() ?: return response

            val retryUrl = originalUrl.newBuilder()
                .addQueryParameter("client_id", refreshedId)
                .build()

            val retryRequest = originalRequest.newBuilder()
                .url(retryUrl)
                .build()

            return chain.proceed(retryRequest)
        }

        return response
    }
}
