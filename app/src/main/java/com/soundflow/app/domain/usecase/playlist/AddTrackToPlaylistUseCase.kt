package com.soundflow.app.domain.usecase.playlist

import com.soundflow.app.domain.repository.PlaylistRepository
import javax.inject.Inject

class AddTrackToPlaylistUseCase @Inject constructor(
    private val repository: PlaylistRepository
) {
    suspend operator fun invoke(playlistId: String, trackId: String) {
        repository.addTrackToPlaylist(playlistId, trackId)
    }
}
