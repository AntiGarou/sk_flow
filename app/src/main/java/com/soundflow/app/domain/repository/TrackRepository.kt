package com.soundflow.app.domain.repository

import com.soundflow.app.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface TrackRepository {
    suspend fun searchTracks(query: String): Flow<List<Track>>
    suspend fun getTrendingTracks(): Flow<List<Track>>
    suspend fun getTrack(id: String): Flow<Track?>
    suspend fun getTracksByGenre(genre: String): Flow<List<Track>>
    suspend fun getDownloadedTracks(): Flow<List<Track>>
    suspend fun getFavoriteTracks(): Flow<List<Track>>
    suspend fun getRecentlyPlayed(): Flow<List<Track>>
    suspend fun toggleFavorite(trackId: String)
    suspend fun updateLastPlayed(trackId: String)
}
