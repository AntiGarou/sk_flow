package com.soundflow.app.domain.usecase.playlist

import com.soundflow.app.domain.model.Playlist
import com.soundflow.app.domain.repository.PlaylistRepository
import javax.inject.Inject

class CreatePlaylistUseCase @Inject constructor(
    private val repository: PlaylistRepository
) {
    suspend operator fun invoke(name: String, description: String? = null): Playlist {
        return repository.createPlaylist(name, description)
    }
}
