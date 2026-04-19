package com.soundflow.app.domain.model

data class Album(
    val id: String,
    val title: String,
    val artistId: String,
    val artworkUrl: String? = null,
    val year: Int? = null,
    val trackCount: Int = 0
)
