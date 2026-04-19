package com.soundflow.app.presentation.screens.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.soundflow.app.domain.model.Playlist
import com.soundflow.app.domain.model.Track
import com.soundflow.app.domain.repository.PlaylistRepository
import com.soundflow.app.domain.usecase.track.GetFavoriteTracksUseCase
import com.soundflow.app.domain.usecase.track.GetRecentlyPlayedUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LibraryUiState(
    val playlists: List<Playlist> = emptyList(),
    val favoriteTracks: List<Track> = emptyList(),
    val recentlyPlayed: List<Track> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val playlistRepository: PlaylistRepository,
    getFavoriteTracksUseCase: GetFavoriteTracksUseCase,
    getRecentlyPlayedUseCase: GetRecentlyPlayedUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LibraryUiState())
    val uiState: StateFlow<LibraryUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val favorites = getFavoriteTracksUseCase()
            val recentlyPlayed = getRecentlyPlayedUseCase()
            val playlists = playlistRepository.getAllPlaylists()

            combine(favorites, recentlyPlayed, playlists) { favs, recent, plists ->
                LibraryUiState(
                    playlists = plists,
                    favoriteTracks = favs,
                    recentlyPlayed = recent,
                    isLoading = false
                )
            }.collect { _uiState.value = it }
        }
    }

    fun createPlaylist(name: String) {
        viewModelScope.launch {
            playlistRepository.createPlaylist(name, null)
        }
    }
}
