package com.soundflow.app.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.soundflow.app.data.local.database.dao.PlaylistDao
import com.soundflow.app.data.local.database.dao.SearchHistoryDao
import com.soundflow.app.data.local.database.dao.TrackDao
import com.soundflow.app.data.local.database.entity.PlaylistEntity
import com.soundflow.app.data.local.database.entity.PlaylistTrackEntity
import com.soundflow.app.data.local.database.entity.RecentlyPlayedEntity
import com.soundflow.app.data.local.database.entity.SearchHistoryEntity
import com.soundflow.app.data.local.database.entity.TrackEntity

@Database(
    entities = [
        TrackEntity::class,
        PlaylistEntity::class,
        PlaylistTrackEntity::class,
        RecentlyPlayedEntity::class,
        SearchHistoryEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class SoundFlowDatabase : RoomDatabase() {
    abstract fun trackDao(): TrackDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun searchHistoryDao(): SearchHistoryDao
}
