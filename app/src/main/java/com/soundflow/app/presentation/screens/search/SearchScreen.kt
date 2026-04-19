package com.soundflow.app.presentation.screens.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.soundflow.app.domain.model.Track
import com.soundflow.app.presentation.components.EmptyState
import com.soundflow.app.presentation.components.GenreCard
import com.soundflow.app.presentation.components.LoadingIndicator
import com.soundflow.app.presentation.components.TrackListItem
import com.soundflow.app.presentation.theme.OnSurface
import com.soundflow.app.presentation.theme.OnSurfaceVariant
import com.soundflow.app.presentation.theme.SurfaceVariant

private val genres = listOf(
    "electronic" to Color(0xFFE91E63),
    "rock" to Color(0xFFFF9800),
    "pop" to Color(0xFF9C27B0),
    "hiphop" to Color(0xFF4CAF50),
    "jazz" to Color(0xFF2196F3),
    "classical" to Color(0xFFFF5722),
    "metal" to Color(0xFF607D8B),
    "ambient" to Color(0xFF009688),
    "folk" to Color(0xFF795548),
    "blues" to Color(0xFF3F51B5),
    "reggae" to Color(0xFFFFC107),
    "latin" to Color(0xFFF44336)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onTrackClick: (Track) -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp)
    ) {
        OutlinedTextField(
            value = uiState.query,
            onValueChange = viewModel::onQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            placeholder = { Text("Search tracks, artists...", color = OnSurfaceVariant) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "Search",
                    tint = OnSurfaceVariant
                )
            },
            shape = RoundedCornerShape(24.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = OnSurfaceVariant,
                cursorColor = MaterialTheme.colorScheme.primary,
                focusedTextColor = MaterialTheme.colorScheme.onBackground,
                unfocusedTextColor = OnSurface
            ),
            singleLine = true
        )

        if (uiState.isSearching) {
            LoadingIndicator()
        } else if (uiState.query.length >= 2) {
            if (uiState.results.isEmpty()) {
                EmptyState(message = "No results found")
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(uiState.results, key = { it.id }) { track ->
                        TrackListItem(
                            track = track,
                            onClick = { onTrackClick(track) }
                        )
                    }
                }
            }
        } else {
            Text(
                text = "Browse All",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(genres) { (genre, color) ->
                    GenreCard(
                        genre = genre.replaceFirstChar { it.uppercase() },
                        color = color,
                        onClick = {
                            viewModel.onQueryChange(genre)
                        }
                    )
                }
            }
        }
    }
}
