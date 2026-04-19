package com.soundflow.app.domain.usecase.track

import com.soundflow.app.domain.model.Track
import com.soundflow.app.domain.repository.TrackRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTrendingTracksUseCase @Inject constructor(
    private val repository: TrackRepository
) {
    suspend operator fun invoke(): Flow<List<Track>> {
        return repository.getTrendingTracks()
    }
}
