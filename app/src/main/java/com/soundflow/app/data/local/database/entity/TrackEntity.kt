package com.soundflow.app.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.soundflow.app.domain.model.TrackSource

@Entity(tableName = "tracks")
data class TrackEntity(
    @PrimaryKey val id: String,
    val title: String,
    val artistId: String,
    val artistName: String,
    val albumId: String? = null,
    val albumName: String? = null,
    val duration: Long = 0L,
    val streamUrl: String? = null,
    val artworkUrl: String? = null,
    val genre: String? = null,
    val source: TrackSource = TrackSource.JAMENDO,
    val localPath: String? = null,
    val isDownloaded: Boolean = false,
    val isFavorite: Boolean = false,
    val lastPlayedAt: Long? = null,
    val playCount: Int = 0
)
