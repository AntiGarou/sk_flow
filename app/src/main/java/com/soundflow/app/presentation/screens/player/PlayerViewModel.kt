package com.soundflow.app.presentation.screens.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.soundflow.app.domain.model.PlaybackState
import com.soundflow.app.domain.model.RepeatMode
import com.soundflow.app.domain.model.Track
import com.soundflow.app.domain.repository.PlayerRepository
import com.soundflow.app.domain.usecase.player.ControlPlaybackUseCase
import com.soundflow.app.domain.usecase.player.PlayTrackUseCase
import com.soundflow.app.domain.usecase.track.ToggleFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val playerRepository: PlayerRepository,
    private val playTrackUseCase: PlayTrackUseCase,
    private val controlPlaybackUseCase: ControlPlaybackUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : ViewModel() {

    val playbackState: StateFlow<PlaybackState> = playerRepository.getPlaybackState()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), PlaybackState())

    fun playTrack(track: Track) {
        viewModelScope.launch { playTrackUseCase(track) }
    }

    fun playTracks(tracks: List<Track>, startIndex: Int = 0) {
        viewModelScope.launch { playTrackUseCase.playTracks(tracks, startIndex) }
    }

    fun playPause() {
        viewModelScope.launch {
            if (playbackState.value.isPlaying) controlPlaybackUseCase.pause()
            else controlPlaybackUseCase.resume()
        }
    }

    fun next() {
        viewModelScope.launch { controlPlaybackUseCase.next() }
    }

    fun previous() {
        viewModelScope.launch { controlPlaybackUseCase.previous() }
    }

    fun seekTo(position: Long) {
        viewModelScope.launch { controlPlaybackUseCase.seekTo(position) }
    }

    fun toggleShuffle() {
        viewModelScope.launch {
            controlPlaybackUseCase.setShuffle(!playbackState.value.shuffleEnabled)
        }
    }

    fun toggleRepeatMode() {
        viewModelScope.launch {
            val nextMode = when (playbackState.value.repeatMode) {
                RepeatMode.OFF -> RepeatMode.ALL
                RepeatMode.ALL -> RepeatMode.ONE
                RepeatMode.ONE -> RepeatMode.OFF
            }
            controlPlaybackUseCase.setRepeatMode(nextMode)
        }
    }

    fun toggleFavorite(trackId: String) {
        viewModelScope.launch { toggleFavoriteUseCase(trackId) }
    }
}
