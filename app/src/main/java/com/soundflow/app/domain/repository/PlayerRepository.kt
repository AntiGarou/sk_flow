package com.soundflow.app.domain.repository

import com.soundflow.app.domain.model.PlaybackState
import com.soundflow.app.domain.model.RepeatMode
import com.soundflow.app.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface PlayerRepository {
    fun getPlaybackState(): Flow<PlaybackState>
    suspend fun play(track: Track)
    suspend fun playTracks(tracks: List<Track>, startIndex: Int = 0)
    suspend fun pause()
    suspend fun resume()
    suspend fun stop()
    suspend fun next()
    suspend fun previous()
    suspend fun seekTo(position: Long)
    suspend fun setShuffle(enabled: Boolean)
    suspend fun setRepeatMode(mode: RepeatMode)
}
