package com.soundflow.app.domain.usecase.player

import com.soundflow.app.domain.model.Track
import com.soundflow.app.domain.repository.PlayerRepository
import javax.inject.Inject

class PlayTrackUseCase @Inject constructor(
    private val repository: PlayerRepository
) {
    suspend operator fun invoke(track: Track) {
        repository.play(track)
    }

    suspend fun playTracks(tracks: List<Track>, startIndex: Int = 0) {
        repository.playTracks(tracks, startIndex)
    }
}
