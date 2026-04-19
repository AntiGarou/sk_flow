package com.soundflow.app.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.soundflow.app.domain.model.Track
import com.soundflow.app.presentation.components.MiniPlayer
import com.soundflow.app.presentation.navigation.NavigationGraph
import com.soundflow.app.presentation.screens.player.FullScreenPlayer
import com.soundflow.app.presentation.screens.player.PlayerViewModel
import com.soundflow.app.presentation.theme.SoundFlowTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SoundFlowTheme {
                val navController = rememberNavController()
                val playerViewModel: PlayerViewModel = hiltViewModel()
                val playbackState by playerViewModel.playbackState.collectAsState()
                var showFullScreenPlayer by remember { mutableStateOf(false) }

                Box(modifier = Modifier.fillMaxSize()) {
                    Scaffold { innerPadding ->
                        Box(modifier = Modifier.fillMaxSize()) {
                            NavigationGraph(
                                navController = navController,
                                onTrackClick = { track: Track ->
                                    playerViewModel.playTrack(track)
                                }
                            )

                            if (playbackState.currentTrack != null) {
                                MiniPlayer(
                                    playbackState = playbackState,
                                    onPlayPause = playerViewModel::playPause,
                                    onClick = { showFullScreenPlayer = true },
                                    modifier = Modifier
                                        .align(androidx.compose.ui.Alignment.BottomCenter)
                                        .padding(bottom = 80.dp)
                                )
                            }
                        }
                    }

                    AnimatedVisibility(
                        visible = showFullScreenPlayer && playbackState.currentTrack != null,
                        enter = slideInVertically(initialOffsetY = { it }),
                        exit = slideOutVertically(targetOffsetY = { it })
                    ) {
                        FullScreenPlayer(
                            playbackState = playbackState,
                            onPlayPause = playerViewModel::playPause,
                            onNext = playerViewModel::next,
                            onPrevious = playerViewModel::previous,
                            onSeek = playerViewModel::seekTo,
                            onShuffleToggle = playerViewModel::toggleShuffle,
                            onRepeatToggle = playerViewModel::toggleRepeatMode,
                            onFavoriteToggle = {
                                playbackState.currentTrack?.let { playerViewModel.toggleFavorite(it.id) }
                            },
                            onDismiss = { showFullScreenPlayer = false }
                        )
                    }
                }
            }
        }
    }
}
