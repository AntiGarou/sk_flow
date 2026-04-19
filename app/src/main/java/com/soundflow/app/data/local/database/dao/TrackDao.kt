package com.soundflow.app.data.local.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.soundflow.app.data.local.database.entity.TrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackDao {
    @Query("SELECT * FROM tracks WHERE isFavorite = 1 ORDER BY title ASC")
    fun getFavoriteTracks(): Flow<List<TrackEntity>>

    @Query("SELECT * FROM tracks WHERE isDownloaded = 1 ORDER BY title ASC")
    fun getDownloadedTracks(): Flow<List<TrackEntity>>

    @Query("SELECT * FROM tracks WHERE id = :id")
    suspend fun getTrackById(id: String): TrackEntity?

    @Query("SELECT * FROM tracks WHERE id IN (SELECT trackId FROM recently_played ORDER BY playedAt DESC) LIMIT :limit")
    fun getRecentlyPlayed(limit: Int = 20): Flow<List<TrackEntity>>

    @Query("SELECT * FROM tracks WHERE genre = :genre ORDER BY playCount DESC LIMIT :limit")
    fun getTracksByGenre(genre: String, limit: Int = 20): Flow<List<TrackEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tracks: List<TrackEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(track: TrackEntity)

    @Update
    suspend fun update(track: TrackEntity)

    @Query("UPDATE tracks SET isFavorite = NOT isFavorite WHERE id = :trackId")
    suspend fun toggleFavorite(trackId: String)

    @Query("UPDATE tracks SET lastPlayedAt = :timestamp, playCount = playCount + 1 WHERE id = :trackId")
    suspend fun updateLastPlayed(trackId: String, timestamp: Long = System.currentTimeMillis())

    @Query("SELECT * FROM tracks WHERE title LIKE '%' || :query || '%' OR artistName LIKE '%' || :query || '%' ORDER BY playCount DESC LIMIT :limit")
    fun searchTracks(query: String, limit: Int = 30): Flow<List<TrackEntity>>

    @Query("DELETE FROM tracks WHERE id = :id")
    suspend fun deleteById(id: String)
}
