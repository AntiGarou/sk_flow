package com.soundflow.app.data.repository

import com.soundflow.app.data.local.database.dao.SearchHistoryDao
import com.soundflow.app.data.local.database.dao.TrackDao
import com.soundflow.app.data.local.database.entity.RecentlyPlayedEntity
import com.soundflow.app.data.local.database.entity.SearchHistoryEntity
import com.soundflow.app.data.mapper.toDomain
import com.soundflow.app.data.mapper.toEntity
import com.soundflow.app.data.remote.api.JamendoApi
import com.soundflow.app.data.remote.api.SoundCloudApi
import com.soundflow.app.data.remote.soundcloud.SoundCloudClientIdProvider
import com.soundflow.app.data.remote.soundcloud.SoundCloudStreamResolver
import com.soundflow.app.domain.model.Track
import com.soundflow.app.domain.repository.TrackRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class TrackRepositoryImpl @Inject constructor(
    private val jamendoApi: JamendoApi,
    private val soundCloudApi: SoundCloudApi,
    private val soundCloudClientIdProvider: SoundCloudClientIdProvider,
    private val soundCloudStreamResolver: SoundCloudStreamResolver,
    private val trackDao: TrackDao,
    private val searchHistoryDao: SearchHistoryDao,
    @Named("jamendoClientId") private val jamendoClientId: String
) : TrackRepository {

    override suspend fun searchTracks(query: String): Flow<List<Track>> = flow {
        try {
            searchHistoryDao.insert(SearchHistoryEntity(query = query))
            val combined = coroutineScope {
                val jamendoDeferred = async {
                    try {
                        val response = jamendoApi.searchTracks(clientId = jamendoClientId, query = query)
                        response.results.map { it.toDomain() }
                    } catch (_: Exception) { emptyList() }
                }

                val soundCloudDeferred = async {
                    try {
                        soundCloudClientIdProvider.getClientId()
                        val response = soundCloudApi.searchTracks(query = query)
                        response.collection.map { it.toDomain() }
                    } catch (_: Exception) { emptyList() }
                }

                jamendoDeferred.await() + soundCloudDeferred.await()
            }

            trackDao.insertAll(combined.map { it.toEntity() })
            emit(combined)
        } catch (e: Exception) {
            val cached = trackDao.searchTracks(query).first().map { it.toDomain() }
            emit(cached)
        }
    }

    override suspend fun getTrendingTracks(): Flow<List<Track>> = flow {
        try {
            val combined = coroutineScope {
                val jamendoDeferred = async {
                    try {
                        val response = jamendoApi.getTracks(clientId = jamendoClientId)
                        response.results.map { it.toDomain() }
                    } catch (_: Exception) { emptyList() }
                }

                val soundCloudDeferred = async {
                    try {
                        soundCloudClientIdProvider.getClientId()
                        val response = soundCloudApi.getTrendingTracks()
                        response.map { it.toDomain() }
                    } catch (_: Exception) { emptyList() }
                }

                jamendoDeferred.await() + soundCloudDeferred.await()
            }

            trackDao.insertAll(combined.map { it.toEntity() })
            emit(combined)
        } catch (e: Exception) {
            val cached = trackDao.getRecentlyPlayed().first().map { it.toDomain() }
            emit(cached)
        }
    }

    override suspend fun getTrack(id: String): Flow<Track?> = flow {
        if (id.startsWith("sc_")) {
            try {
                val scId = id.removePrefix("sc_").toLongOrNull() ?: run {
                    emit(trackDao.getTrackById(id)?.toDomain())
                    return@flow
                }
                soundCloudClientIdProvider.getClientId()
                val dto = soundCloudApi.getTrack(scId)
                val streamUrl = soundCloudStreamResolver.resolveStreamUrl(dto)
                val track = dto.toDomain().copy(streamUrl = streamUrl ?: dto.stream_url)
                emit(track)
            } catch (_: Exception) {
                emit(trackDao.getTrackById(id)?.toDomain())
            }
        } else {
            val entity = trackDao.getTrackById(id)
            emit(entity?.toDomain())
        }
    }

    override suspend fun getTracksByGenre(genre: String): Flow<List<Track>> = flow {
        try {
            val combined = coroutineScope {
                val jamendoDeferred = async {
                    try {
                        val response = jamendoApi.getTracksByGenre(clientId = jamendoClientId, genre = genre)
                        response.results.map { it.toDomain() }
                    } catch (_: Exception) { emptyList() }
                }

                val soundCloudDeferred = async {
                    try {
                        soundCloudClientIdProvider.getClientId()
                        val response = soundCloudApi.searchTracks(query = genre, limit = 20)
                        response.collection.map { it.toDomain() }
                    } catch (_: Exception) { emptyList() }
                }

                jamendoDeferred.await() + soundCloudDeferred.await()
            }

            trackDao.insertAll(combined.map { it.toEntity() })
            emit(combined)
        } catch (e: Exception) {
            val cached = trackDao.getTracksByGenre(genre).first().map { it.toDomain() }
            emit(cached)
        }
    }

    override suspend fun getDownloadedTracks(): Flow<List<Track>> {
        return trackDao.getDownloadedTracks().map { list -> list.map { it.toDomain() } }
    }

    override suspend fun getFavoriteTracks(): Flow<List<Track>> {
        return trackDao.getFavoriteTracks().map { list -> list.map { it.toDomain() } }
    }

    override suspend fun getRecentlyPlayed(): Flow<List<Track>> {
        return trackDao.getRecentlyPlayed().map { list -> list.map { it.toDomain() } }
    }

    override suspend fun toggleFavorite(trackId: String) {
        trackDao.toggleFavorite(trackId)
    }

    override suspend fun updateLastPlayed(trackId: String) {
        trackDao.updateLastPlayed(trackId)
        searchHistoryDao.insertRecentlyPlayed(RecentlyPlayedEntity(trackId = trackId))
    }
}
