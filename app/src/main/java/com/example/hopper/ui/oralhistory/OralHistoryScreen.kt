package com.example.hopper.ui.oralhistory

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.hopper.data.local.db.entity.OralHistoryEntity

/**
 * Displays community-contributed oral histories tied to pandals, with the
 * ability to download their audio for offline listening.
 */
@Composable
fun OralHistoryScreen(
    viewModel: OralHistoryViewModel = hiltViewModel()
) {
    val histories by viewModel.histories.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Oral Histories",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))

        if (histories.isEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "No oral histories available yet",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            Spacer(modifier = Modifier.height(12.dp))
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(histories, key = { it.id }) { history ->
                    OralHistoryCard(
                        history = history,
                        onDownload = { url -> viewModel.downloadAudio(history.id, url) }
                    )
                }
            }
        }
    }
}

@Composable
private fun OralHistoryCard(
    history: OralHistoryEntity,
    onDownload: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = history.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = history.contributorName ?: "Unknown contributor",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${history.year}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            history.textContent?.let { text ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Audio playback placeholder — wiring an actual player is TODO.
                IconButton(onClick = { /* TODO: play audio */ }) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play audio"
                    )
                }

                OfflineDownloadControl(
                    isCached = history.isAudioCachedLocally,
                    audioUrl = history.audioUrl,
                    onDownload = onDownload
                )
            }
        }
    }
}

@Composable
private fun OfflineDownloadControl(
    isCached: Boolean,
    audioUrl: String?,
    onDownload: (String) -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        if (isCached) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Available offline",
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Offline",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
        } else {
            IconButton(
                onClick = { audioUrl?.let(onDownload) },
                enabled = audioUrl != null
            ) {
                Icon(
                    imageVector = Icons.Default.CloudDownload,
                    contentDescription = "Download for offline"
                )
            }
        }
    }
}
