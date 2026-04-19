package com.soundflow.app.domain.usecase.track

import com.soundflow.app.domain.repository.TrackRepository
import javax.inject.Inject

class ToggleFavoriteUseCase @Inject constructor(
    private val repository: TrackRepository
) {
    suspend operator fun invoke(trackId: String) {
        repository.toggleFavorite(trackId)
    }
}
