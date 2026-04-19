package com.soundflow.app.data.remote.soundcloud

import com.soundflow.app.data.remote.api.SoundCloudApi
import com.soundflow.app.data.remote.dto.SoundCloudTrackDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SoundCloudStreamResolver @Inject constructor(
    private val soundCloudApi: SoundCloudApi,
    private val clientIdProvider: SoundCloudClientIdProvider
) {
    suspend fun resolveStreamUrl(track: SoundCloudTrackDto): String? {
        val transcodings = track.media?.transcodings ?: return track.stream_url

        val progressive = transcodings.firstOrNull {
            it.format?.protocol == "progressive" &&
            (it.format?.mime_type?.startsWith("audio/mpeg") == true ||
             it.format?.mime_type?.startsWith("audio/ogg") == true)
        } ?: transcodings.firstOrNull {
            it.format?.protocol == "progressive"
        } ?: return track.stream_url

        val transcodeUrl = progressive.url
        if (transcodeUrl.isBlank()) return track.stream_url

        return try {
            val clientId = clientIdProvider.getClientId()
            val urlWithClientId = if (clientId != null && !transcodeUrl.contains("client_id")) {
                val separator = if (transcodeUrl.contains("?")) "&" else "?"
                "$transcodeUrl${separator}client_id=$clientId"
            } else {
                transcodeUrl
            }
            val response = soundCloudApi.getStreamUrl(urlWithClientId)
            response.url.ifBlank { null } ?: track.stream_url
        } catch (e: Exception) {
            track.stream_url
        }
    }
}
