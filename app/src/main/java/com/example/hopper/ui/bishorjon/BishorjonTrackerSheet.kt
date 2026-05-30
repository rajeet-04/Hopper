package com.example.hopper.ui.bishorjon

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.hopper.data.local.db.entity.ProcessionEntity
import com.example.hopper.domain.model.LatLng
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Modal bottom sheet listing active bishorjon processions and allowing
 * users to crowd-report sightings. Sightings power proximity alerts that
 * trigger when a procession comes within 500m of the user.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BishorjonTrackerSheet(
    onDismiss: () -> Unit,
    viewModel: BishorjonTrackerViewModel = hiltViewModel()
) {
    val processions by viewModel.processions.collectAsState()

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, bottom = 24.dp)
        ) {
            Text(
                text = "Bishorjon Tracker",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Proximity alerts trigger when a procession comes within 500m of you.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (processions.isEmpty()) {
                Text(
                    text = "No active processions right now.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(processions, key = { it.id }) { procession ->
                        ProcessionCard(
                            procession = procession,
                            onReportSighting = {
                                // TODO: wire to real GPS location instead of dummy coordinates
                                viewModel.reportSighting(
                                    procession.id,
                                    LatLng(22.86, 88.36)
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProcessionCard(
    procession: ProcessionEntity,
    onReportSighting: () -> Unit
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
                text = procession.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Starts: ${formatTime(procession.startTimeEpochMs)}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Est. ends: ${
                    procession.estimatedEndTimeEpochMs?.let { formatTime(it) } ?: "Unknown"
                }",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onReportSighting,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Report Sighting")
            }
        }
    }
}

private val timeFormatter = SimpleDateFormat("EEE, d MMM h:mm a", Locale.getDefault())

private fun formatTime(epochMs: Long): String = timeFormatter.format(Date(epochMs))
