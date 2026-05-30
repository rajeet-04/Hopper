package com.example.hopper.ui.volunteer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.hopper.data.local.db.entity.VolunteerPostEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Displays open volunteer opportunities for the active festival. Contact info
 * is never shown publicly — only the role, location, timing, and remaining
 * spots are surfaced.
 */
@Composable
fun VolunteerScreen(
    viewModel: VolunteerViewModel = hiltViewModel()
) {
    val posts by viewModel.posts.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Volunteer",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))

        if (posts.isEmpty()) {
            Text(
                text = "No volunteer opportunities right now",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(posts, key = { it.id }) { post ->
                    VolunteerPostCard(
                        post = post,
                        onSignUp = {
                            // Contact info is collected privately; placeholder values for MVP.
                            viewModel.signUp(post.id, "Anonymous", "")
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun VolunteerPostCard(
    post: VolunteerPostEntity,
    onSignUp: () -> Unit
) {
    val spotsRemaining = (post.volunteersNeeded - post.volunteersSignedUp).coerceAtLeast(0)

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
                text = post.role,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = post.location,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Date: ${formatDate(post.date)}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "Time: ${formatTime(post.timeSlotStart)} – ${formatTime(post.timeSlotEnd)}",
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (post.isFilled) "Filled" else "$spotsRemaining spots left",
                    style = MaterialTheme.typography.labelLarge,
                    color = if (post.isFilled) {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    } else {
                        MaterialTheme.colorScheme.primary
                    }
                )
                Button(
                    onClick = onSignUp,
                    enabled = !post.isFilled
                ) {
                    Text("Sign Up")
                }
            }
        }
    }
}

private fun formatDate(epochMillis: Long): String =
    SimpleDateFormat("EEE, d MMM yyyy", Locale.getDefault()).format(Date(epochMillis))

private fun formatTime(epochMillis: Long): String =
    SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(epochMillis))
