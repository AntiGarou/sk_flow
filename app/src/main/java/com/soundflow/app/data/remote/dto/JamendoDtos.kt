package com.soundflow.app.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class JamendTrackDto(
    val id: String = "",
    val name: String = "",
    val duration: Double = 0.0,
    val artist_id: String = "",
    val artist_name: String = "",
    val artist_idstr: String = "",
    val artist_image: String? = null,
    val album_id: String? = null,
    val album_name: String? = null,
    val album_image: String? = null,
    val image: String? = null,
    val audio: String? = null,
    val audiodownload: String? = null,
    val prourl: String? = null,
    val shorturl: String? = null,
    val musicinfo: MusicInfoDto? = null
)

@Serializable
data class MusicInfoDto(
    val tags: List<String> = emptyList(),
    val genres: List<String> = emptyList()
)

@Serializable
data class JamendoTracksResponse(
    val headers: JamendoHeadersDto? = null,
    val results: List<JamendTrackDto> = emptyList()
)

@Serializable
data class JamendoHeadersDto(
    val status: String = "",
    val code: Int = 0,
    val error_message: String? = null
)

@Serializable
data class JamendoArtistDto(
    val id: String = "",
    val name: String = "",
    val image: String? = null,
    val shorturl: String? = null
)

@Serializable
data class JamendoArtistsResponse(
    val results: List<JamendoArtistDto> = emptyList()
)
