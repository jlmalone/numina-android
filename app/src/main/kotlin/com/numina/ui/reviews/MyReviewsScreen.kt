package com.numina.ui.reviews

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.numina.data.models.ReviewWithClass
import com.numina.ui.components.EmptyState
import com.numina.ui.components.ErrorScreen
import com.numina.ui.components.LoadingScreen
import com.numina.ui.reviews.components.ReviewItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyReviewsScreen(
    onNavigateBack: () -> Unit,
    onEditReview: (String) -> Unit,
    onClassClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MyReviewsViewModel = hiltViewModel()
) {
    val uiState by viewModel.myReviewsState.collectAsState()
    var reviewToDelete by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Reviews") },
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
            uiState.isLoading && uiState.reviews.isEmpty() -> {
                LoadingScreen(modifier = Modifier.padding(paddingValues))
            }
            uiState.error != null && uiState.reviews.isEmpty() -> {
                ErrorScreen(
                    message = uiState.error ?: "Unknown error",
                    onRetry = { viewModel.retryMyReviews() },
                    modifier = Modifier.padding(paddingValues)
                )
            }
            uiState.reviews.isEmpty() -> {
                EmptyState(
                    message = "You haven't written any reviews yet",
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
                    items(uiState.reviews, key = { it.review.id }) { reviewWithClass ->
                        MyReviewCard(
                            reviewWithClass = reviewWithClass,
                            onEdit = { onEditReview(reviewWithClass.review.id) },
                            onDelete = { reviewToDelete = reviewWithClass.review.id },
                            onClassClick = { onClassClick(reviewWithClass.fitnessClass.id) },
                            isDeleting = uiState.deleteInProgress == reviewWithClass.review.id
                        )
                    }
                }
            }
        }
    }

    // Delete confirmation dialog
    if (reviewToDelete != null) {
        AlertDialog(
            onDismissRequest = { reviewToDelete = null },
            title = { Text("Delete Review") },
            text = { Text("Are you sure you want to delete this review? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        reviewToDelete?.let { viewModel.deleteReview(it) }
                        reviewToDelete = null
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { reviewToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun MyReviewCard(
    reviewWithClass: ReviewWithClass,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onClassClick: () -> Unit,
    isDeleting: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = onClassClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Class info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = reviewWithClass.fitnessClass.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = reviewWithClass.fitnessClass.type,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Action buttons
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (reviewWithClass.review.canEdit) {
                        IconButton(onClick = onEdit) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit review"
                            )
                        }
                    }
                    if (reviewWithClass.review.canDelete) {
                        IconButton(
                            onClick = onDelete,
                            enabled = !isDeleting
                        ) {
                            if (isDeleting) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp)
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete review",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }

            Divider()

            // Review content
            ReviewItem(
                review = reviewWithClass.review,
                onHelpfulClick = { /* Not interactive in my reviews */ }
            )
        }
    }
}
