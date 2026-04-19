package com.soundflow.app.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class SoundCloudTrackDto(
    val id: Long = 0,
    val title: String = "",
    val description: String? = null,
    val duration: Long = 0,
    val permalink_url: String? = null,
    val artwork_url: String? = null,
    val stream_url: String? = null,
    val download_url: String? = null,
    val genre: String? = null,
    val user: SoundCloudUserDto? = null,
    val playback_count: Long = 0,
    val favoritings_count: Long = 0,
    val downloadable: Boolean = false,
    val has_downloads_left: Boolean = false,
    val media: SoundCloudMediaDto? = null
)

@Serializable
data class SoundCloudUserDto(
    val id: Long = 0,
    val username: String = "",
    val avatar_url: String? = null,
    val permalink_url: String? = null
)

@Serializable
data class SoundCloudMediaDto(
    val transcodings: List<SoundCloudTranscodingDto> = emptyList()
)

@Serializable
data class SoundCloudTranscodingDto(
    val url: String = "",
    val format: SoundCloudFormatDto? = null,
    val quality: String = "",
    val preset: String = ""
)

@Serializable
data class SoundCloudFormatDto(
    val protocol: String = "",
    val mime_type: String = ""
)

@Serializable
data class SoundCloudSearchResponse(
    val collection: List<SoundCloudTrackDto> = emptyList(),
    val next_href: String? = null,
    val total_results: Int = 0
)

@Serializable
data class SoundCloudStreamResponse(
    val url: String = ""
)
