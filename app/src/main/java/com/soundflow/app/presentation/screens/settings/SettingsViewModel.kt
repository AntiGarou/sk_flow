package com.soundflow.app.presentation.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.soundflow.app.data.local.preferences.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val audioQuality: String = "mp32",
    val downloadQuality: String = "mp32"
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferences: UserPreferences
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = combine(
        userPreferences.audioQuality,
        userPreferences.downloadQuality
    ) { audio, download ->
        SettingsUiState(audioQuality = audio, downloadQuality = download)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SettingsUiState())

    fun setAudioQuality(quality: String) {
        viewModelScope.launch { userPreferences.setAudioQuality(quality) }
    }

    fun setDownloadQuality(quality: String) {
        viewModelScope.launch { userPreferences.setDownloadQuality(quality) }
    }
}
