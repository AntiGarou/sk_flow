package com.soundflow.app.domain.model

data class Artist(
    val id: String,
    val name: String,
    val avatarUrl: String? = null,
    val bio: String? = null
)
