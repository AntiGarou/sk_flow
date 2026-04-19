package com.soundflow.app.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.soundflow.app.domain.model.Track
import com.soundflow.app.presentation.theme.OnSurface
import com.soundflow.app.presentation.theme.OnSurfaceVariant
import com.soundflow.app.presentation.theme.Primary

@Composable
fun TrackListItem(
    track: Track,
    onClick: () -> Unit,
    onMore: () -> Unit = {},
    onFavorite: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = track.artworkUrl,
            contentDescription = track.title,
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(4.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = track.title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = track.artist.name,
                style = MaterialTheme.typography.labelSmall,
                color = OnSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        if (onFavorite != null) {
            IconButton(onClick = onFavorite) {
                Icon(
                    imageVector = if (track.isFavorite) Icons.Filled.Favorite
                    else Icons.Filled.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (track.isFavorite) Primary else OnSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        IconButton(onClick = onMore) {
            Icon(
                imageVector = Icons.Filled.MoreVert,
                contentDescription = "More",
                tint = OnSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
