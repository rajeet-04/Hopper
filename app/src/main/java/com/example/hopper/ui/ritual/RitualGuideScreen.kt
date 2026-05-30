package com.example.hopper.ui.ritual

import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.hopper.data.local.db.entity.RitualGuideEntity
import org.json.JSONArray

/**
 * Displays ritual guides as expandable cards. Each card shows the title (and
 * Bengali title) collapsed, and reveals numbered steps plus timing notes when
 * expanded.
 */
@Composable
fun RitualGuideScreen(
    viewModel: RitualGuideViewModel = hiltViewModel()
) {
    val guides by viewModel.guides.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Ritual Guides",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))

        if (guides.isEmpty()) {
            Text(
                text = "No ritual guides available yet",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(guides, key = { it.id }) { guide ->
                    RitualGuideCard(guide = guide)
                }
            }
        }
    }
}

@Composable
private fun RitualGuideCard(guide: RitualGuideEntity) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = guide.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    guide.titleBengali?.let { bengali ->
                        Text(
                            text = bengali,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (expanded) "Collapse" else "Expand"
                )
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(12.dp))

                val steps = remember(guide.steps) { parseSteps(guide.steps) }
                if (steps.isNotEmpty()) {
                    Text(
                        text = "Steps",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    steps.forEachIndexed { index, step ->
                        Row(modifier = Modifier.padding(vertical = 2.dp)) {
                            Text(
                                text = "${index + 1}.",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = step,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                guide.timingNotes?.let { notes ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Timing",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = notes,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                if (guide.audioAssetId != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Audio download placeholder — wiring the actual asset is TODO.
                        IconButton(onClick = { /* TODO: download ritual audio */ }) {
                            Icon(
                                imageVector = Icons.Default.CloudDownload,
                                contentDescription = "Download audio guide"
                            )
                        }
                        Text(
                            text = "Audio guide",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }
        }
    }
}

/**
 * Parses the steps column, which is stored as a JSON array of strings, into a
 * list. Returns an empty list if the value is null or not valid JSON.
 */
private fun parseSteps(stepsJson: String?): List<String> {
    if (stepsJson.isNullOrBlank()) return emptyList()
    return try {
        val array = JSONArray(stepsJson)
        List(array.length()) { index -> array.getString(index) }
    } catch (e: Exception) {
        emptyList()
    }
}
