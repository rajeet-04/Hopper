package com.example.hopper.ui.crowd

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.hopper.domain.model.CrowdBucket
import com.example.hopper.ui.theme.CrowdGreen
import com.example.hopper.ui.theme.CrowdRed
import com.example.hopper.ui.theme.CrowdYellow

/**
 * Bottom sheet for reporting crowd levels. Presents three large selectable
 * buckets (GREEN/YELLOW/RED); tapping one submits immediately so a report takes
 * at most two interactions (open sheet, tap bucket). Shows the reporter's badge
 * tier and surfaces rate-limit feedback.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrowdReportSheet(
    pandalId: String,
    onDismiss: () -> Unit,
    viewModel: CrowdReportViewModel = hiltViewModel()
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val submitState by viewModel.submitState.collectAsStateWithLifecycle()
    val badgeTier by viewModel.badgeTier.collectAsStateWithLifecycle()

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
                text = "How's the crowd?",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            ReporterBadge(badgeTier)

            CrowdBucket.entries.forEach { bucket ->
                CrowdBucketButton(
                    bucket = bucket,
                    onClick = { viewModel.submitReport(pandalId, bucket) }
                )
            }

            SubmitFeedback(submitState)
        }
    }
}

@Composable
private fun ReporterBadge(badgeTier: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = "Reporter tier:",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = badgeTier,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun CrowdBucketButton(
    bucket: CrowdBucket,
    onClick: () -> Unit
) {
    val color = bucketColor(bucket)
    Surface(
        color = color,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(88.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = bucketTitle(bucket),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = bucket.label,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
                Text(
                    text = "~${bucket.waitMinutes} min wait",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun SubmitFeedback(submitState: SubmitState) {
    when (submitState) {
        SubmitState.Idle -> Unit
        SubmitState.Success -> FeedbackText(
            text = "Thanks! Your report was submitted.",
            color = CrowdGreen
        )
        SubmitState.RateLimited -> FeedbackText(
            text = "You reported recently. Try again in a few minutes.",
            color = CrowdYellow
        )
        is SubmitState.Error -> FeedbackText(
            text = submitState.message,
            color = CrowdRed
        )
    }
}

@Composable
private fun FeedbackText(text: String, color: Color) {
    Surface(
        color = color.copy(alpha = 0.16f),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
        )
    }
}

private fun bucketColor(bucket: CrowdBucket): Color = when (bucket) {
    CrowdBucket.GREEN -> CrowdGreen
    CrowdBucket.YELLOW -> CrowdYellow
    CrowdBucket.RED -> CrowdRed
}

private fun bucketTitle(bucket: CrowdBucket): String = when (bucket) {
    CrowdBucket.GREEN -> "Light"
    CrowdBucket.YELLOW -> "Moderate"
    CrowdBucket.RED -> "Heavy"
}
