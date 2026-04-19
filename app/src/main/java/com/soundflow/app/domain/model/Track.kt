package com.soundflow.app.domain.model

data class Track(
    val id: String,
    val title: String,
    val artist: Artist,
    val album: Album? = null,
    val duration: Long = 0L,
    val streamUrl: String? = null,
    val artworkUrl: String? = null,
    val genre: String? = null,
    val source: TrackSource = TrackSource.JAMENDO,
    val isDownloadable: Boolean = false,
    val isFavorite: Boolean = false,
    val localPath: String? = null
)
