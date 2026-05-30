package com.example.hopper.ui.exit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.hopper.domain.model.ExitNodeCategory
import com.example.hopper.domain.model.ExitRoute
import com.example.hopper.ui.theme.MedicalRed
import com.example.hopper.ui.theme.MetroGreen
import com.example.hopper.ui.theme.PoliceBlue
import com.example.hopper.ui.theme.RailwayOrange
import kotlin.math.roundToInt

/**
 * Bottom sheet listing the nearest exit per category (Metro, Railway, Police,
 * Medical) with a category-colored indicator, walking distance and estimated
 * time. Routes flagged [ExitRoute.isAlternate] surface an alternate-route note.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExitRouterSheet(
    pandalId: String?,
    onDismiss: () -> Unit,
    viewModel: ExitRouterViewModel = hiltViewModel()
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val routes by viewModel.routes.collectAsStateWithLifecycle()

    LaunchedEffect(pandalId) {
        if (pandalId != null) {
            viewModel.loadRoutes(pandalId, nightSafetyMode = false)
        } else {
            viewModel.loadNearestExits()
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Nearest Exits",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            if (routes.isEmpty()) {
                Text(
                    text = "Locating exit routes…",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                val nearestPerCategory = ExitNodeCategory.entries.mapNotNull { category ->
                    routes
                        .filter { it.exitNode.category == category }
                        .minByOrNull { it.distanceMeters }
                }
                nearestPerCategory.forEach { route ->
                    ExitRouteRow(route)
                }
            }
        }
    }
}

@Composable
private fun ExitRouteRow(route: ExitRoute) {
    val color = categoryColor(route.exitNode.category)
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = categoryLabel(route.exitNode.category),
                    style = MaterialTheme.typography.labelMedium,
                    color = color,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = route.exitNode.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${formatDistance(route.distanceMeters)} · ${route.estimatedWalkingMinutes} min walk",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (route.isAlternate) {
                    Text(
                        text = "Alternate routes available",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

private fun categoryColor(category: ExitNodeCategory): Color = when (category) {
    ExitNodeCategory.METRO -> MetroGreen
    ExitNodeCategory.RAILWAY -> RailwayOrange
    ExitNodeCategory.POLICE -> PoliceBlue
    ExitNodeCategory.MEDICAL -> MedicalRed
}

private fun categoryLabel(category: ExitNodeCategory): String = when (category) {
    ExitNodeCategory.METRO -> "Metro"
    ExitNodeCategory.RAILWAY -> "Railway"
    ExitNodeCategory.POLICE -> "Police"
    ExitNodeCategory.MEDICAL -> "Medical"
}

private fun formatDistance(meters: Double): String {
    return if (meters >= 1000) {
        val km = (meters / 100).roundToInt() / 10.0
        "$km km"
    } else {
        "${meters.roundToInt()} m"
    }
}
