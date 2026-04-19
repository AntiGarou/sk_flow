package com.soundflow.app.domain.repository

import com.soundflow.app.domain.model.Track
import kotlinx.coroutines.flow.Flow

data class DownloadProgress(val progress: Float, val isComplete: Boolean, val error: Throwable? = null)

interface DownloadRepository {
    suspend fun downloadTrack(track: Track): Flow<DownloadProgress>
    suspend fun deleteDownload(trackId: String)
    suspend fun getDownloadedTracks(): Flow<List<Track>>
    suspend fun isDownloaded(trackId: String): Boolean
}
