package com.soundflow.app.domain.repository

import com.soundflow.app.domain.model.Playlist
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    suspend fun getAllPlaylists(): Flow<List<Playlist>>
    suspend fun getPlaylist(id: String): Flow<Playlist?>
    suspend fun createPlaylist(name: String, description: String?): Playlist
    suspend fun updatePlaylist(playlist: Playlist)
    suspend fun deletePlaylist(id: String)
    suspend fun addTrackToPlaylist(playlistId: String, trackId: String)
    suspend fun removeTrackFromPlaylist(playlistId: String, trackId: String)
    suspend fun reorderTracks(playlistId: String, trackIds: List<String>)
}
