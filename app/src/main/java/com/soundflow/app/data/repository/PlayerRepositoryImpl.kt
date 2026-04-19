package com.soundflow.app.data.repository

import android.content.ComponentName
import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.soundflow.app.data.local.database.dao.TrackDao
import com.soundflow.app.data.mapper.toDomain
import com.soundflow.app.data.mapper.toEntity
import com.soundflow.app.data.remote.api.SoundCloudApi
import com.soundflow.app.data.remote.soundcloud.SoundCloudClientIdProvider
import com.soundflow.app.domain.model.PlaybackState
import com.soundflow.app.domain.model.RepeatMode
import com.soundflow.app.domain.model.Track
import com.soundflow.app.domain.model.TrackSource
import com.soundflow.app.domain.repository.PlayerRepository
import com.soundflow.app.service.PlaybackService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlayerRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val exoPlayer: ExoPlayer,
    private val trackDao: TrackDao,
    private val soundCloudApi: SoundCloudApi,
    private val soundCloudClientIdProvider: SoundCloudClientIdProvider
) : PlayerRepository {

    private val _playbackState = MutableStateFlow(PlaybackState())
    private val playbackState: StateFlow<PlaybackState> = _playbackState.asStateFlow()

    private var queue: List<Track> = emptyList()
    private var queueIndex: Int = 0

    private val listener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            _playbackState.value = _playbackState.value.copy(isPlaying = isPlaying)
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            if (playbackState == Player.STATE_READY) {
                _playbackState.value = _playbackState.value.copy(
                    position = exoPlayer.currentPosition,
                    duration = exoPlayer.duration.coerceAtLeast(0L)
                )
            }
        }
    }

    init {
        exoPlayer.addListener(listener)

        CoroutineScope(Dispatchers.Main).launch {
            while (true) {
                if (exoPlayer.isPlaying) {
                    _playbackState.value = _playbackState.value.copy(
                        position = exoPlayer.currentPosition,
                        duration = exoPlayer.duration.coerceAtLeast(0L)
                    )
                }
                kotlinx.coroutines.delay(200)
            }
        }
    }

    override fun getPlaybackState(): Flow<PlaybackState> = playbackState

    override suspend fun play(track: Track) {
        queue = listOf(track)
        queueIndex = 0
        playMediaItem(track)
        _playbackState.value = _playbackState.value.copy(
            currentTrack = track,
            queue = queue,
            queueIndex = 0
        )
        trackDao.updateLastPlayed(track.id)
    }

    override suspend fun playTracks(tracks: List<Track>, startIndex: Int) {
        queue = tracks
        queueIndex = startIndex
        playMediaItem(tracks[startIndex])
        _playbackState.value = _playbackState.value.copy(
            currentTrack = tracks[startIndex],
            queue = queue,
            queueIndex = startIndex
        )
        trackDao.updateLastPlayed(tracks[startIndex].id)
    }

    override suspend fun pause() {
        exoPlayer.pause()
    }

    override suspend fun resume() {
        exoPlayer.play()
    }

    override suspend fun stop() {
        exoPlayer.stop()
        _playbackState.value = PlaybackState()
    }

    override suspend fun next() {
        if (queueIndex < queue.size - 1) {
            queueIndex++
            playMediaItem(queue[queueIndex])
            _playbackState.value = _playbackState.value.copy(
                currentTrack = queue[queueIndex],
                queueIndex = queueIndex
            )
            trackDao.updateLastPlayed(queue[queueIndex].id)
        }
    }

    override suspend fun previous() {
        if (exoPlayer.currentPosition > 3000) {
            exoPlayer.seekTo(0)
        } else if (queueIndex > 0) {
            queueIndex--
            playMediaItem(queue[queueIndex])
            _playbackState.value = _playbackState.value.copy(
                currentTrack = queue[queueIndex],
                queueIndex = queueIndex
            )
            trackDao.updateLastPlayed(queue[queueIndex].id)
        }
    }

    override suspend fun seekTo(position: Long) {
        exoPlayer.seekTo(position)
        _playbackState.value = _playbackState.value.copy(position = position)
    }

    override suspend fun setShuffle(enabled: Boolean) {
        exoPlayer.shuffleModeEnabled = enabled
        _playbackState.value = _playbackState.value.copy(shuffleEnabled = enabled)
    }

    override suspend fun setRepeatMode(mode: RepeatMode) {
        exoPlayer.repeatMode = when (mode) {
            RepeatMode.OFF -> Player.REPEAT_MODE_OFF
            RepeatMode.ONE -> Player.REPEAT_MODE_ONE
            RepeatMode.ALL -> Player.REPEAT_MODE_ALL
        }
        _playbackState.value = _playbackState.value.copy(repeatMode = mode)
    }

    private suspend fun playMediaItem(track: Track) {
        var streamUrl = track.streamUrl ?: track.localPath ?: return

        if (track.source == TrackSource.SOUNDCLOUD && streamUrl.contains("api-v2.soundcloud.com")) {
            streamUrl = resolveSoundCloudUrl(streamUrl) ?: streamUrl
        }

        val mediaItem = MediaItem.fromUri(streamUrl)
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.play()
    }

    private suspend fun resolveSoundCloudUrl(transcodingUrl: String): String? {
        return try {
            val clientId = soundCloudClientIdProvider.getClientId() ?: return null
            val separator = if (transcodingUrl.contains("?")) "&" else "?"
            val urlWithClientId = "$transcodingUrl${separator}client_id=$clientId"
            val response = soundCloudApi.getStreamUrl(urlWithClientId)
            response.url.ifBlank { null }
        } catch (_: Exception) {
            null
        }
    }
}
