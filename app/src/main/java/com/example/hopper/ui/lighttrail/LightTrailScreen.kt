package com.example.hopper.ui.lighttrail

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.hopper.data.local.db.entity.LightTrailEntity

/**
 * Displays the Chandannagar Light Trail — an ordered walk of illuminated
 * installations available during Jagaddhatri Puja.
 */
@Composable
fun LightTrailScreen(
    viewModel: LightTrailViewModel = hiltViewModel()
) {
    val trailStops by viewModel.trailStops.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Chandannagar Light Trail",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))

        if (trailStops.isEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Light Trail is available during Jagaddhatri Puja",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            Text(
                text = "${trailStops.size} stops along the trail",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(trailStops, key = { it.id }) { stop ->
                    LightTrailStopCard(stop = stop)
                }
            }
        }
    }
}

@Composable
private fun LightTrailStopCard(
    stop: LightTrailEntity
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            SequenceBadge(sequence = stop.sequenceOrder)
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stop.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                stop.artistName?.let { artist ->
                    Text(
                        text = "Artist: $artist",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                stop.dimensions?.let { dimensions ->
                    Text(
                        text = "Dimensions: $dimensions",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                stop.themeDescription?.let { theme ->
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = theme,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                if (stop.isVantagePoint) {
                    Spacer(modifier = Modifier.height(8.dp))
                    VantagePointChip(viewingAngle = stop.viewingAngle)
                }
            }
        }
    }
}

@Composable
private fun SequenceBadge(sequence: Int) {
    Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(40.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = "$sequence",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Composable
private fun VantagePointChip(viewingAngle: Float?) {
    val angleText = viewingAngle?.let { " · best viewed at ${it.toInt()}°" } ?: ""
    AssistChip(
        onClick = {},
        enabled = false,
        label = { Text("Vantage Point$angleText") },
        colors = AssistChipDefaults.assistChipColors(
            disabledLabelColor = MaterialTheme.colorScheme.primary
        )
    )
}
