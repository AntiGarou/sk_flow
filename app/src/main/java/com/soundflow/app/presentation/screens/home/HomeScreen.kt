package com.soundflow.app.presentation.screens.home

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.soundflow.app.domain.model.Track
import com.soundflow.app.presentation.components.EmptyState
import com.soundflow.app.presentation.components.LoadingIndicator
import com.soundflow.app.presentation.components.TrackCard
import com.soundflow.app.presentation.components.TrackListItem
import com.soundflow.app.presentation.theme.OnSurface
import com.soundflow.app.presentation.theme.Primary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onTrackClick: (Track) -> Unit,
    onSeeAllClick: (String) -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 8.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = getGreeting(),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        if (uiState.isLoading) {
            LoadingIndicator()
            return
        }

        if (uiState.trendingTracks.isEmpty() && uiState.recentlyPlayed.isEmpty()) {
            EmptyState(message = "No music found. Check your connection!")
            return
        }

        if (uiState.recentlyPlayed.isNotEmpty()) {
            SectionHeader(title = "Recently Played", onSeeAll = { onSeeAllClick("recently_played") })
            LazyRow(
                contentPadding = PaddingValues(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.recentlyPlayed, key = { it.id }) { track ->
                    TrackCard(track = track, onClick = { onTrackClick(track) })
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        if (uiState.trendingTracks.isNotEmpty()) {
            SectionHeader(title = "Trending Now", onSeeAll = { onSeeAllClick("trending") })
            LazyRow(
                contentPadding = PaddingValues(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.trendingTracks, key = { it.id }) { track ->
                    TrackCard(track = track, onClick = { onTrackClick(track) })
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        uiState.genreTracks.forEach { (genre, tracks) ->
            if (tracks.isNotEmpty()) {
                SectionHeader(title = genre.replaceFirstChar { it.uppercase() }, onSeeAll = { })
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(tracks, key = { it.id }) { track ->
                        TrackCard(track = track, onClick = { onTrackClick(track) })
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
private fun SectionHeader(title: String, onSeeAll: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        TextButton(onClick = onSeeAll) {
            Text(text = "See all", color = Primary)
        }
    }
}

private fun getGreeting(): String {
    val hour = SimpleDateFormat("HH", Locale.getDefault()).format(Date()).toInt()
    return when {
        hour < 12 -> "Good Morning"
        hour < 18 -> "Good Afternoon"
        else -> "Good Evening"
    }
}
