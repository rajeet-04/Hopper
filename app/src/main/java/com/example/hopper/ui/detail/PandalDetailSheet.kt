package com.example.hopper.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import com.example.hopper.domain.model.ArtisanCredits
import com.example.hopper.domain.model.ConfidenceLevel
import com.example.hopper.domain.model.CrowdBucket
import com.example.hopper.domain.model.Pandal
import com.example.hopper.domain.model.SourceType
import com.example.hopper.ui.theme.CrowdGreen
import com.example.hopper.ui.theme.CrowdRed
import com.example.hopper.ui.theme.CrowdYellow

/**
 * Bottom sheet presenting rich detail for a single pandal: theme, committee,
 * artisan credits, photo gallery, awards, data provenance, confidence and an
 * archive timeline stub. A crowd indicator is shown at the top.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PandalDetailSheet(
    pandalId: String,
    onDismiss: () -> Unit,
    viewModel: PandalDetailViewModel = hiltViewModel()
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val pandal by viewModel.pandal.collectAsStateWithLifecycle()
    val crowdBucket by viewModel.crowdBucket.collectAsStateWithLifecycle()

    LaunchedEffect(pandalId) {
        viewModel.loadPandal(pandalId)
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
            CrowdIndicator(crowdBucket)

            val current = pandal
            if (current == null) {
                Text(
                    text = "Loading pandal…",
                    style = MaterialTheme.typography.bodyLarge
                )
            } else {
                PandalDetailContent(current)
            }
        }
    }
}

@Composable
private fun CrowdIndicator(bucket: CrowdBucket?) {
    val (color, text) = when (bucket) {
        CrowdBucket.GREEN -> CrowdGreen to "Crowd: ${bucket.label}"
        CrowdBucket.YELLOW -> CrowdYellow to "Crowd: ${bucket.label}"
        CrowdBucket.RED -> CrowdRed to "Crowd: ${bucket.label}"
        null -> MaterialTheme.colorScheme.outline to "Crowd: no recent reports"
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(14.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun PandalDetailContent(pandal: Pandal) {
    // Theme (headline)
    Text(
        text = pandal.theme ?: pandal.name,
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.Bold
    )
    pandal.nameBengali?.let {
        Text(
            text = it,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    // Committee + established year
    pandal.committeeName?.let { committee ->
        LabeledRow(label = "Committee", value = committee)
    }
    pandal.establishedYear?.let { year ->
        LabeledRow(label = "Established", value = year.toString())
    }

    HorizontalDivider()

    // Artisan credits
    ArtisanCreditsSection(pandal.artisanCredits)

    // Photo gallery
    PhotoGallery(pandal.photos)

    // Awards
    AwardsSection(pandal.awards)

    HorizontalDivider()

    // Data provenance + confidence
    DataProvenanceSection(pandal.sourceType, pandal.confidenceLevel)

    HorizontalDivider()

    // Archive timeline stub
    Text(
        text = "Archive Timeline",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold
    )
    Text(
        text = "Archive timeline coming soon",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun LabeledRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "$label:",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun ArtisanCreditsSection(credits: ArtisanCredits?) {
    Text(
        text = "Artisan Credits",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold
    )
    if (credits == null ||
        (credits.idolMaker == null && credits.lightingDesigner == null && credits.themeDesigner == null)
    ) {
        Text(
            text = "No artisan credits recorded",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        return
    }
    credits.idolMaker?.let { LabeledRow(label = "Idol Maker", value = it) }
    credits.lightingDesigner?.let { LabeledRow(label = "Lighting Designer", value = it) }
    credits.themeDesigner?.let { LabeledRow(label = "Theme Designer", value = it) }
}

@Composable
private fun PhotoGallery(photos: List<String>) {
    Text(
        text = "Photos",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold
    )
    if (photos.isEmpty()) {
        Text(
            text = "No photos available",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        return
    }
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(photos.take(10)) { _ ->
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )
        }
    }
}

@Composable
private fun AwardsSection(awards: List<String>) {
    Text(
        text = "Awards",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold
    )
    if (awards.isEmpty()) {
        Text(
            text = "No awards listed",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        return
    }
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        awards.forEach { award ->
            Text(
                text = "• $award",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun DataProvenanceSection(
    sourceType: SourceType,
    confidenceLevel: ConfidenceLevel
) {
    val provenanceLabel = if (sourceType == SourceType.COMMITTEE) {
        "Committee Verified"
    } else {
        "Community Sourced"
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = provenanceLabel,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )
        ConfidenceBadge(confidenceLevel)
    }
}

@Composable
private fun ConfidenceBadge(confidenceLevel: ConfidenceLevel) {
    val color = when (confidenceLevel) {
        ConfidenceLevel.LOW -> CrowdRed
        ConfidenceLevel.MEDIUM -> CrowdYellow
        ConfidenceLevel.HIGH -> CrowdGreen
    }
    Surface(
        color = color.copy(alpha = 0.18f),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Text(
                text = "${confidenceLevel.name} confidence",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
