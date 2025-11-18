package com.numina.ui.reviews

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.numina.data.models.PendingReview
import com.numina.ui.components.EmptyState
import com.numina.ui.components.ErrorScreen
import com.numina.ui.components.LoadingScreen
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PendingReviewsScreen(
    onNavigateBack: () -> Unit,
    onWriteReview: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MyReviewsViewModel = hiltViewModel()
) {
    val uiState by viewModel.pendingReviewsState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pending Reviews") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        when {
            uiState.isLoading && uiState.pendingReviews.isEmpty() -> {
                LoadingScreen(modifier = Modifier.padding(paddingValues))
            }
            uiState.error != null && uiState.pendingReviews.isEmpty() -> {
                ErrorScreen(
                    message = uiState.error ?: "Unknown error",
                    onRetry = { viewModel.retryPendingReviews() },
                    modifier = Modifier.padding(paddingValues)
                )
            }
            uiState.pendingReviews.isEmpty() -> {
                EmptyState(
                    message = "No classes waiting for review",
                    modifier = Modifier.padding(paddingValues)
                )
            }
            else -> {
                LazyColumn(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(uiState.pendingReviews, key = { it.classId }) { pendingReview ->
                        PendingReviewCard(
                            pendingReview = pendingReview,
                            onWriteReview = { onWriteReview(pendingReview.classId) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PendingReviewCard(
    pendingReview: PendingReview,
    onWriteReview: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Class image
            if (pendingReview.classImageUrl != null) {
                AsyncImage(
                    model = pendingReview.classImageUrl,
                    contentDescription = "Class image",
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .then(
                            Modifier.then(
                                Modifier.then(
                                    Modifier.wrapContentSize()
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        modifier = Modifier.size(80.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {}
                }
            }

            // Class info and action button
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = pendingReview.className,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = pendingReview.classType,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Trainer: ${pendingReview.trainerName}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Attended: ${formatDate(pendingReview.attendedAt)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Button(
                    onClick = onWriteReview,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.RateReview,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Write Review")
                }
            }
        }
    }
}

private fun formatDate(isoDate: String): String {
    return try {
        val instant = Instant.parse(isoDate)
        val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")
            .withZone(ZoneId.systemDefault())
        formatter.format(instant)
    } catch (e: Exception) {
        isoDate
    }
}
