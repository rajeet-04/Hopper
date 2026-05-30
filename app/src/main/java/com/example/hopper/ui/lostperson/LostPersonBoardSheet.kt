package com.example.hopper.ui.lostperson

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.hopper.data.local.db.entity.LostPersonPostEntity

/**
 * Bottom sheet for the Lost Person board. Lets users post their current location
 * and browse active posts within 2km sorted by recency, each with an expiry
 * countdown and a resolve action. No account required.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LostPersonBoardSheet(
    onDismiss: () -> Unit,
    viewModel: LostPersonBoardViewModel = hiltViewModel()
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val posts by viewModel.posts.collectAsStateWithLifecycle()

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
                text = "Lost Person Board",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "No account required",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Button(
                onClick = { viewModel.postMyLocation("My Location", "") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Post My Location")
            }

            val sortedPosts = posts.sortedByDescending { it.reportedAtEpochMs }
            if (sortedPosts.isEmpty()) {
                Text(
                    text = "No active posts within 2km.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(sortedPosts, key = { it.id }) { post ->
                        LostPersonPostRow(
                            post = post,
                            onResolve = { viewModel.resolvePost(post.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LostPersonPostRow(
    post: LostPersonPostEntity,
    onResolve: () -> Unit
) {
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
                text = post.displayName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = post.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = expiryLabel(post.expiresAtEpochMs),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
                OutlinedButton(onClick = onResolve) {
                    Text("Resolve")
                }
            }
        }
    }
}

private fun expiryLabel(expiresAtEpochMs: Long): String {
    val remainingMs = expiresAtEpochMs - System.currentTimeMillis()
    if (remainingMs <= 0) {
        return "Expired"
    }
    val minutes = remainingMs / 60_000L
    return "Expires in $minutes min"
}
