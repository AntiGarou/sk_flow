package com.soundflow.app.data.repository

import android.content.Context
import android.os.Environment
import com.soundflow.app.data.local.database.dao.TrackDao
import com.soundflow.app.data.mapper.toDomain
import com.soundflow.app.data.mapper.toEntity
import com.soundflow.app.domain.model.Track
import com.soundflow.app.domain.repository.DownloadProgress
import com.soundflow.app.domain.repository.DownloadRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.File
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DownloadRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val trackDao: TrackDao
) : DownloadRepository {

    override suspend fun downloadTrack(track: Track): Flow<DownloadProgress> = flow {
        emit(DownloadProgress(progress = 0f, isComplete = false))
        try {
            val streamUrl = track.streamUrl ?: throw IllegalArgumentException("No stream URL")
            val dir = File(context.getExternalFilesDir(Environment.DIRECTORY_MUSIC), "downloads")
            if (!dir.exists()) dir.mkdirs()
            val file = File(dir, "${track.id}.mp3")

            withContext(Dispatchers.IO) {
                URL(streamUrl).openStream().use { input ->
                    file.outputStream().use { output ->
                        val buffer = ByteArray(8192)
                        var bytesRead: Int
                        var totalBytesRead = 0L
                        val contentLength = input.available().toLong()

                        while (input.read(buffer).also { bytesRead = it } != -1) {
                            output.write(buffer, 0, bytesRead)
                            totalBytesRead += bytesRead
                            if (contentLength > 0) {
                                val progress = totalBytesRead.toFloat() / contentLength
                                emit(DownloadProgress(progress = progress, isComplete = false))
                            }
                        }
                    }
                }
            }

            val updatedEntity = track.toEntity().copy(isDownloaded = true, localPath = file.absolutePath)
            trackDao.insert(updatedEntity)
            emit(DownloadProgress(progress = 1f, isComplete = true))
        } catch (e: Exception) {
            emit(DownloadProgress(progress = 0f, isComplete = false, error = e))
        }
    }

    override suspend fun deleteDownload(trackId: String) {
        val entity = trackDao.getTrackById(trackId) ?: return
        entity.localPath?.let { path ->
            val file = File(path)
            if (file.exists()) file.delete()
        }
        trackDao.insert(entity.copy(isDownloaded = false, localPath = null))
    }

    override suspend fun getDownloadedTracks(): Flow<List<Track>> {
        return trackDao.getDownloadedTracks().map { list -> list.map { it.toDomain() } }
    }

    override suspend fun isDownloaded(trackId: String): Boolean {
        return trackDao.getTrackById(trackId)?.isDownloaded == true
    }
}
