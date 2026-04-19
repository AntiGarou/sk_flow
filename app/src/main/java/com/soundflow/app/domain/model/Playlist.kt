package com.soundflow.app.domain.model

data class Playlist(
    val id: String,
    val name: String,
    val description: String? = null,
    val artworkUrl: String? = null,
    val tracks: List<Track> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isDownloaded: Boolean = false
)
