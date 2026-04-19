package com.soundflow.app.presentation.screens.library

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.soundflow.app.domain.model.Playlist
import com.soundflow.app.domain.model.Track
import com.soundflow.app.presentation.components.EmptyState
import com.soundflow.app.presentation.components.LoadingIndicator
import com.soundflow.app.presentation.components.PlaylistCard
import com.soundflow.app.presentation.components.TrackListItem
import com.soundflow.app.presentation.theme.OnSurface
import com.soundflow.app.presentation.theme.OnSurfaceVariant
import com.soundflow.app.presentation.theme.Primary
import com.soundflow.app.presentation.theme.SurfaceVariant

@Composable
fun LibraryScreen(
    onPlaylistClick: (Playlist) -> Unit,
    onTrackClick: (Track) -> Unit,
    viewModel: LibraryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }

    if (showCreateDialog) {
        CreatePlaylistDialog(
            onDismiss = { showCreateDialog = false },
            onCreate = { name ->
                viewModel.createPlaylist(name)
                showCreateDialog = false
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Your Library",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            IconButton(onClick = { showCreateDialog = true }) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Create Playlist",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        if (uiState.isLoading) {
            LoadingIndicator()
            return
        }

        LazyColumn(
            contentPadding = PaddingValues(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            item {
                LibrarySectionHeader(
                    icon = { Icon(Icons.Filled.Favorite, contentDescription = null, tint = Primary) },
                    title = "Liked Songs",
                    subtitle = "${uiState.favoriteTracks.size} tracks"
                )
            }

            if (uiState.favoriteTracks.isNotEmpty()) {
                items(uiState.favoriteTracks.take(5), key = { it.id }) { track ->
                    TrackListItem(track = track, onClick = { onTrackClick(track) })
                }
            }

            item {
                Spacer(modifier = Modifier.height(12.dp))
                LibrarySectionHeader(
                    icon = { Icon(Icons.Filled.History, contentDescription = null, tint = OnSurfaceVariant) },
                    title = "Recently Played",
                    subtitle = "${uiState.recentlyPlayed.size} tracks"
                )
            }

            if (uiState.recentlyPlayed.isNotEmpty()) {
                items(uiState.recentlyPlayed.take(5), key = { it.id }) { track ->
                    TrackListItem(track = track, onClick = { onTrackClick(track) })
                }
            }

            item {
                Spacer(modifier = Modifier.height(12.dp))
                LibrarySectionHeader(
                    icon = { Icon(Icons.Filled.Download, contentDescription = null, tint = OnSurfaceVariant) },
                    title = "Your Playlists",
                    subtitle = "${uiState.playlists.size} playlists"
                )
            }

            if (uiState.playlists.isNotEmpty()) {
                items(uiState.playlists, key = { it.id }) { playlist ->
                    PlaylistListItem(
                        playlist = playlist,
                        onClick = { onPlaylistClick(playlist) }
                    )
                }
            }

            if (uiState.favoriteTracks.isEmpty() && uiState.recentlyPlayed.isEmpty() && uiState.playlists.isEmpty()) {
                item {
                    EmptyState(message = "Your library is empty. Start listening!")
                }
            }
        }
    }
}

@Composable
private fun LibrarySectionHeader(
    icon: @Composable () -> Unit,
    title: String,
    subtitle: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon()
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = title, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onBackground)
            Text(text = subtitle, style = MaterialTheme.typography.labelSmall, color = OnSurface)
        }
    }
}

@Composable
private fun PlaylistListItem(
    playlist: Playlist,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = playlist.artworkUrl,
            contentDescription = playlist.name,
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(4.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = playlist.name, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onBackground, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(text = "${playlist.tracks.size} tracks", style = MaterialTheme.typography.labelSmall, color = OnSurface)
        }
    }
}

@Composable
private fun CreatePlaylistDialog(
    onDismiss: () -> Unit,
    onCreate: (String) -> Unit
) {
    var name by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create Playlist") },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                placeholder = { Text("Playlist name") },
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(
                onClick = { if (name.isNotBlank()) onCreate(name) },
                enabled = name.isNotBlank()
            ) {
                Text("Create", color = Primary)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = OnSurface)
            }
        }
    )
}
