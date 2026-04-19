package com.soundflow.app.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.soundflow.app.domain.model.Resource
import com.soundflow.app.domain.model.Track
import com.soundflow.app.domain.usecase.track.GetRecentlyPlayedUseCase
import com.soundflow.app.domain.usecase.track.GetTrendingTracksUseCase
import com.soundflow.app.domain.usecase.track.GetTracksByGenreUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val trendingTracks: List<Track> = emptyList(),
    val recentlyPlayed: List<Track> = emptyList(),
    val genreTracks: Map<String, List<Track>> = emptyMap(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getTrendingTracksUseCase: GetTrendingTracksUseCase,
    private val getRecentlyPlayedUseCase: GetRecentlyPlayedUseCase,
    private val getTracksByGenreUseCase: GetTracksByGenreUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState(isLoading = true))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadContent()
    }

    fun loadContent() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                getTrendingTracksUseCase().collect { tracks ->
                    _uiState.value = _uiState.value.copy(trendingTracks = tracks)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }

            try {
                getRecentlyPlayedUseCase().collect { tracks ->
                    _uiState.value = _uiState.value.copy(recentlyPlayed = tracks)
                }
            } catch (_: Exception) { }

            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }

    fun loadGenre(genre: String) {
        viewModelScope.launch {
            try {
                getTracksByGenreUseCase(genre).collect { tracks ->
                    val current = _uiState.value.genreTracks.toMutableMap()
                    current[genre] = tracks
                    _uiState.value = _uiState.value.copy(genreTracks = current)
                }
            } catch (_: Exception) { }
        }
    }
}
