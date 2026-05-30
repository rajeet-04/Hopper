package com.example.hopper.ui.bhog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.hopper.data.local.db.entity.BhogPinEntity

/**
 * Bottom sheet for the Bhog Finder. Lets users toggle between bhog distribution
 * and street food pins, browse nearby pins sorted by distance, and drop a quick
 * report at their current location. No account required.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BhogFinderSheet(
    onDismiss: () -> Unit,
    viewModel: BhogFinderViewModel = hiltViewModel()
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val category by viewModel.category.collectAsStateWithLifecycle()
    val pins by viewModel.pins.collectAsStateWithLifecycle()

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
                text = "Bhog Finder",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FilterChip(
                    selected = category == BhogFinderViewModel.CATEGORY_BHOG_DISTRIBUTION,
                    onClick = {
                        viewModel.setCategory(BhogFinderViewModel.CATEGORY_BHOG_DISTRIBUTION)
                    },
                    label = { Text("Bhog Distribution") }
                )
                FilterChip(
                    selected = category == BhogFinderViewModel.CATEGORY_STREET_FOOD,
                    onClick = {
                        viewModel.setCategory(BhogFinderViewModel.CATEGORY_STREET_FOOD)
                    },
                    label = { Text("Street Food") }
                )
            }

            if (pins.isEmpty()) {
                Text(
                    text = "No pins nearby yet. Be the first to report one.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(pins, key = { it.id }) { pin ->
                        BhogPinRow(pin)
                    }
                }
            }

            Button(
                onClick = { viewModel.submitQuickReport("New Pin") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Report a pin here")
            }
        }
    }
}

@Composable
private fun BhogPinRow(pin: BhogPinEntity) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = pin.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            pin.description?.let { description ->
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            pin.rating?.let { rating ->
                Text(
                    text = "★ ${"%.1f".format(rating)}",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
