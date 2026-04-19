package com.soundflow.app.domain.usecase.player

import com.soundflow.app.domain.model.RepeatMode
import com.soundflow.app.domain.repository.PlayerRepository
import javax.inject.Inject

class ControlPlaybackUseCase @Inject constructor(
    private val repository: PlayerRepository
) {
    suspend fun pause() = repository.pause()
    suspend fun resume() = repository.resume()
    suspend fun stop() = repository.stop()
    suspend fun next() = repository.next()
    suspend fun previous() = repository.previous()
    suspend fun seekTo(position: Long) = repository.seekTo(position)
    suspend fun setShuffle(enabled: Boolean) = repository.setShuffle(enabled)
    suspend fun setRepeatMode(mode: RepeatMode) = repository.setRepeatMode(mode)
}
