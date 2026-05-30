package com.example.hopper.ui.map

import androidx.compose.foundation.background
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.hopper.domain.model.CrowdBucket
import com.example.hopper.domain.model.ExitNode
import com.example.hopper.domain.model.ExitNodeCategory
import com.example.hopper.domain.model.Pandal
import com.example.hopper.ui.theme.CrowdGreen
import com.example.hopper.ui.theme.CrowdRed
import com.example.hopper.ui.theme.CrowdYellow
import com.example.hopper.ui.theme.MedicalRed
import com.example.hopper.ui.theme.MetroGreen
import com.example.hopper.ui.theme.PoliceBlue
import com.example.hopper.ui.theme.RailwayOrange

data class NearbyPandalInfo(
    val pandal: Pandal,
    val distanceMeters: Double,
    val crowdBucket: CrowdBucket?,
    val bearing: Float
)

data class NearbyExitInfo(
    val exitNode: ExitNode,
    val distanceMeters: Double,
    val bearing: Float
)

/**
 * Graceful degradation view shown when map tiles are unavailable.
 * Displays a compass/radar-style directional indicator with nearest pandals
 * and exit nodes in a list format.
 */
@Composable
fun GracefulDegradationView(
    nearbyPandals: List<NearbyPandalInfo>,
    nearbyExits: List<NearbyExitInfo>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Offline Mode",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Map tiles unavailable. Showing directions based on GPS.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Nearest Pandals",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(nearbyPandals) { info ->
                PandalDirectionCard(info)
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Nearest Exits",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(nearbyExits) { info ->
                ExitDirectionCard(info)
            }
        }
    }
}

@Composable
private fun PandalDirectionCard(info: NearbyPandalInfo) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CrowdIndicator(info.crowdBucket)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = info.pandal.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = formatDistance(info.distanceMeters),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            DirectionArrow(bearing = info.bearing)
        }
    }
}

@Composable
private fun ExitDirectionCard(info: NearbyExitInfo) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ExitCategoryIndicator(info.exitNode.category)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = info.exitNode.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "${info.exitNode.category.name} • ${formatDistance(info.distanceMeters)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            DirectionArrow(bearing = info.bearing)
        }
    }
}

@Composable
private fun CrowdIndicator(bucket: CrowdBucket?) {
    val color = when (bucket) {
        CrowdBucket.GREEN -> CrowdGreen
        CrowdBucket.YELLOW -> CrowdYellow
        CrowdBucket.RED -> CrowdRed
        null -> Color.Gray
    }
    Box(
        modifier = Modifier
            .size(12.dp)
            .clip(CircleShape)
            .background(color)
    )
}

@Composable
private fun ExitCategoryIndicator(category: ExitNodeCategory) {
    val color = when (category) {
        ExitNodeCategory.METRO -> MetroGreen
        ExitNodeCategory.RAILWAY -> RailwayOrange
        ExitNodeCategory.POLICE -> PoliceBlue
        ExitNodeCategory.MEDICAL -> MedicalRed
    }
    Box(
        modifier = Modifier
            .size(12.dp)
            .clip(CircleShape)
            .background(color)
    )
}

@Composable
private fun DirectionArrow(bearing: Float) {
    Text(
        text = bearingToArrow(bearing),
        style = MaterialTheme.typography.titleLarge
    )
}

private fun bearingToArrow(bearing: Float): String {
    val normalized = ((bearing % 360) + 360) % 360
    return when {
        normalized < 22.5 || normalized >= 337.5 -> "↑"
        normalized < 67.5 -> "↗"
        normalized < 112.5 -> "→"
        normalized < 157.5 -> "↘"
        normalized < 202.5 -> "↓"
        normalized < 247.5 -> "↙"
        normalized < 292.5 -> "←"
        else -> "↖"
    }
}

private fun formatDistance(meters: Double): String {
    return if (meters < 1000) {
        "${meters.toInt()} m"
    } else {
        "%.1f km".format(meters / 1000.0)
    }
}
