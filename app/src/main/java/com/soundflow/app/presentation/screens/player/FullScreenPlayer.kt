package com.soundflow.app.presentation.screens.player

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.PauseCircle
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.soundflow.app.domain.model.PlaybackState
import com.soundflow.app.domain.model.RepeatMode
import com.soundflow.app.presentation.theme.OnSurface
import com.soundflow.app.presentation.theme.OnSurfaceVariant
import com.soundflow.app.presentation.theme.Primary

@Composable
fun FullScreenPlayer(
    playbackState: PlaybackState,
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onSeek: (Long) -> Unit,
    onShuffleToggle: () -> Unit,
    onRepeatToggle: () -> Unit,
    onFavoriteToggle: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val track = playbackState.currentTrack ?: return

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(onClick = onDismiss, modifier = Modifier.align(Alignment.Start)) {
            Icon(
                imageVector = Icons.Filled.KeyboardArrowDown,
                contentDescription = "Minimize",
                tint = OnSurface,
                modifier = Modifier.size(32.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        AsyncImage(
            model = track.artworkUrl,
            contentDescription = track.title,
            modifier = Modifier
                .size(300.dp)
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = track.title,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = track.artist.name,
                    style = MaterialTheme.typography.bodyLarge,
                    color = OnSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            IconButton(onClick = onFavoriteToggle) {
                Icon(
                    imageVector = if (track.isFavorite) Icons.Filled.Favorite
                    else Icons.Filled.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (track.isFavorite) Primary else OnSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        var sliderPosition by remember(playbackState.position) {
            mutableStateOf(playbackState.position.toFloat())
        }

        Slider(
            value = sliderPosition,
            onValueChange = { sliderPosition = it },
            onValueChangeFinished = {
                onSeek(sliderPosition.toLong())
            },
            valueRange = 0f..(playbackState.duration.toFloat().coerceAtLeast(1f)),
            colors = SliderDefaults.colors(
                thumbColor = Primary,
                activeTrackColor = Primary
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = formatDuration(playbackState.position),
                style = MaterialTheme.typography.labelSmall,
                color = OnSurface
            )
            Text(
                text = formatDuration(playbackState.duration),
                style = MaterialTheme.typography.labelSmall,
                color = OnSurface
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onShuffleToggle) {
                Icon(
                    imageVector = Icons.Filled.Shuffle,
                    contentDescription = "Shuffle",
                    tint = if (playbackState.shuffleEnabled) Primary else OnSurfaceVariant
                )
            }

            IconButton(onClick = onPrevious) {
                Icon(
                    imageVector = Icons.Filled.SkipPrevious,
                    contentDescription = "Previous",
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.size(36.dp)
                )
            }

            IconButton(onClick = onPlayPause) {
                Icon(
                    imageVector = if (playbackState.isPlaying)
                        Icons.Filled.PauseCircle
                    else
                        Icons.Filled.PlayCircle,
                    contentDescription = if (playbackState.isPlaying) "Pause" else "Play",
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.size(64.dp)
                )
            }

            IconButton(onClick = onNext) {
                Icon(
                    imageVector = Icons.Filled.SkipNext,
                    contentDescription = "Next",
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.size(36.dp)
                )
            }

            IconButton(onClick = onRepeatToggle) {
                Icon(
                    imageVector = when (playbackState.repeatMode) {
                        RepeatMode.ONE -> Icons.Filled.RepeatOne
                        else -> Icons.Filled.Repeat
                    },
                    contentDescription = "Repeat",
                    tint = if (playbackState.repeatMode != RepeatMode.OFF) Primary else OnSurfaceVariant
                )
            }
        }
    }
}

private fun formatDuration(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%d:%02d".format(minutes, seconds)
}
