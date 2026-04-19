package com.soundflow.app.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.soundflow.app.presentation.theme.OnSurfaceVariant

@Composable
fun EmptyState(
    message: String,
    icon: @Composable () -> Unit = {
        Icon(
            imageVector = Icons.Filled.MusicNote,
            contentDescription = null,
            tint = OnSurfaceVariant,
            modifier = Modifier.size(64.dp)
        )
    },
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.foundation.layout.Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            icon()
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(16.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = OnSurfaceVariant
            )
        }
    }
}
