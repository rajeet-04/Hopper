package com.example.hopper.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.hopper.domain.usecase.HourlyPrediction
import com.example.hopper.ui.theme.CrowdGreen
import com.example.hopper.ui.theme.CrowdRed
import com.example.hopper.ui.theme.CrowdYellow

@Composable
fun HeatTimelineBar(
    predictions: List<HourlyPrediction>,
    peakSummary: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Crowd Prediction",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(4.dp)),
            horizontalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            predictions.forEach { prediction ->
                val color = when (prediction.predictedBucket) {
                    CrowdBucket.GREEN -> CrowdGreen
                    CrowdBucket.YELLOW -> CrowdYellow
                    CrowdBucket.RED -> CrowdRed
                }
                val borderColor = if (prediction.isLive) MaterialTheme.colorScheme.primary else Color.Transparent

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(24.dp)
                        .background(color)
                        .then(
                            if (prediction.isLive) Modifier.border(2.dp, borderColor) else Modifier
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (prediction.isLive) {
                        Text("▼", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Hour labels
        Row(modifier = Modifier.fillMaxWidth()) {
            predictions.forEachIndexed { index, prediction ->
                if (index % 2 == 0) {
                    Box(modifier = Modifier.weight(2f)) {
                        Text(
                            text = formatHour(prediction.hour),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = peakSummary,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun formatHour(hour: Int): String {
    val h = if (hour > 24) hour - 24 else hour
    return when {
        h == 0 || h == 24 -> "12A"
        h < 12 -> "${h}P"
        h == 12 -> "12P"
        else -> "${h - 12}P"
    }
}
