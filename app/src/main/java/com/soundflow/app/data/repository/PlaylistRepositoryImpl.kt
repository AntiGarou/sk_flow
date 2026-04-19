package com.soundflow.app.data.repository

import com.soundflow.app.data.local.database.dao.PlaylistDao
import com.soundflow.app.data.local.database.dao.TrackDao
import com.soundflow.app.data.local.database.entity.PlaylistEntity
import com.soundflow.app.data.local.database.entity.PlaylistTrackEntity
import com.soundflow.app.data.mapper.toDomain
import com.soundflow.app.domain.model.Playlist
import com.soundflow.app.domain.model.Track
import com.soundflow.app.domain.repository.PlaylistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaylistRepositoryImpl @Inject constructor(
    private val playlistDao: PlaylistDao,
    private val trackDao: TrackDao
) : PlaylistRepository {

    override suspend fun getAllPlaylists(): Flow<List<Playlist>> {
        return playlistDao.getAllPlaylists().map { list ->
            list.map { it.toPlaylistDomain() }
        }
    }

    override suspend fun getPlaylist(id: String): Flow<Playlist?> {
        return playlistDao.getAllPlaylists().map { playlists ->
            playlists.firstOrNull { it.id == id }?.toPlaylistDomain()
        }
    }

    override suspend fun createPlaylist(name: String, description: String?): Playlist {
        val entity = PlaylistEntity(
            id = UUID.randomUUID().toString(),
            name = name,
            description = description
        )
        playlistDao.insert(entity)
        return entity.toPlaylistDomain()
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        playlistDao.update(
            id = playlist.id,
            name = playlist.name,
            description = playlist.description
        )
    }

    override suspend fun deletePlaylist(id: String) {
        playlistDao.deleteById(id)
    }

    override suspend fun addTrackToPlaylist(playlistId: String, trackId: String) {
        val currentTracks = playlistDao.getPlaylistTracks(playlistId).first()
        val nextPosition = currentTracks.maxOfOrNull { it.position }?.plus(1) ?: 0
        playlistDao.insertPlaylistTrack(
            PlaylistTrackEntity(
                playlistId = playlistId,
                trackId = trackId,
                position = nextPosition
            )
        )
    }

    override suspend fun removeTrackFromPlaylist(playlistId: String, trackId: String) {
        playlistDao.removeTrackFromPlaylist(playlistId, trackId)
    }

    override suspend fun reorderTracks(playlistId: String, trackIds: List<String>) {
        playlistDao.clearPlaylistTracks(playlistId)
        val entities = trackIds.mapIndexed { index, trackId ->
            PlaylistTrackEntity(playlistId = playlistId, trackId = trackId, position = index)
        }
        playlistDao.insertPlaylistTracks(entities)
    }

    private fun PlaylistEntity.toPlaylistDomain() = Playlist(
        id = id,
        name = name,
        description = description,
        artworkUrl = artworkUrl,
        createdAt = createdAt,
        updatedAt = updatedAt,
        isDownloaded = isDownloaded
    )
}
