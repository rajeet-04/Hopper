package com.example.hopper.ui.heritage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.hopper.data.local.db.entity.HeritagePointEntity

/** Sepia/brown accent used across the heritage experience. */
private val HeritageSepia = Color(0xFF8D6E63)

/**
 * Modal bottom sheet showing the details of a single heritage point.
 *
 * The point is resolved by [heritagePointId] from the festival-gated list.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeritageDetailSheet(
    heritagePointId: String?,
    onDismiss: () -> Unit,
    viewModel: HeritageViewModel = hiltViewModel()
) {
    val heritagePoints by viewModel.heritagePoints.collectAsState()
    val point = heritagePoints.firstOrNull { it.id == heritagePointId }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(start = 16.dp, end = 16.dp, bottom = 24.dp)
        ) {
            if (point == null) {
                Text(
                    text = "Heritage point not found.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                HeritageDetailContent(point = point)
            }
        }
    }
}

@Composable
private fun HeritageDetailContent(point: HeritagePointEntity) {
    PhotoPlaceholder()
    Spacer(modifier = Modifier.height(16.dp))

    Text(
        text = point.name,
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold,
        color = HeritageSepia
    )
    point.nameBengali?.let { bengali ->
        Text(
            text = bengali,
            style = MaterialTheme.typography.titleMedium,
            color = HeritageSepia
        )
    }
    point.period?.let { period ->
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = period,
            style = MaterialTheme.typography.labelLarge,
            fontStyle = FontStyle.Italic,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    point.description?.let { description ->
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = description,
            style = MaterialTheme.typography.bodyLarge
        )
    }
    point.descriptionBengali?.let { descriptionBengali ->
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = descriptionBengali,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun PhotoPlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)
            .clip(RoundedCornerShape(12.dp))
            .background(HeritageSepia.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Photo",
            style = MaterialTheme.typography.titleMedium,
            color = HeritageSepia
        )
    }
}
