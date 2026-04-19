package com.soundflow.app.data.mapper

import com.soundflow.app.data.remote.dto.SoundCloudTrackDto
import com.soundflow.app.data.remote.dto.SoundCloudTranscodingDto
import com.soundflow.app.domain.model.Album
import com.soundflow.app.domain.model.Artist
import com.soundflow.app.domain.model.Track
import com.soundflow.app.domain.model.TrackSource

fun SoundCloudTrackDto.toDomain(streamResolver: ((SoundCloudTrackDto) -> String?)? = null): Track {
    val artwork = artwork_url?.replace("-large.", "-t500x500.")

    val streamUrl = if (media?.transcodings?.isNotEmpty() == true) {
        val progressive = media.transcodings.firstOrNull {
            it.format?.protocol == "progressive" && it.format?.mime_type?.startsWith("audio/mpeg") == true
        }
        progressive?.url ?: media.transcodings.firstOrNull {
            it.format?.protocol == "progressive"
        }?.url ?: stream_url
    } else {
        stream_url
    }

    return Track(
        id = "sc_$id",
        title = title,
        artist = Artist(
            id = "sc_${user?.id ?: 0}",
            name = user?.username ?: "Unknown",
            avatarUrl = user?.avatar_url
        ),
        album = null,
        duration = duration,
        streamUrl = streamUrl,
        artworkUrl = artwork,
        genre = genre,
        source = TrackSource.SOUNDCLOUD,
        isDownloadable = downloadable && has_downloads_left
    )
}
