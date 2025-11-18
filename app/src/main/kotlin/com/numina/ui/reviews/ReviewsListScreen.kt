package com.numina.ui.reviews

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.numina.data.models.ReviewSortBy
import com.numina.ui.components.EmptyState
import com.numina.ui.components.ErrorScreen
import com.numina.ui.components.LoadingScreen
import com.numina.ui.reviews.components.RatingSummary
import com.numina.ui.reviews.components.ReviewItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewsListScreen(
    classId: String,
    onNavigateBack: () -> Unit,
    onWriteReview: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ReviewsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showSortMenu by remember { mutableStateOf(false) }

    LaunchedEffect(classId) {
        viewModel.loadReviews(classId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reviews") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    TextButton(onClick = { showSortMenu = true }) {
                        Text(
                            text = when (uiState.sortBy) {
                                ReviewSortBy.RECENT -> "Recent"
                                ReviewSortBy.HELPFUL -> "Helpful"
                                ReviewSortBy.RATING_HIGH -> "Rating: High"
                                ReviewSortBy.RATING_LOW -> "Rating: Low"
                            }
                        )
                    }

                    DropdownMenu(
                        expanded = showSortMenu,
                        onDismissRequest = { showSortMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Recent") },
                            onClick = {
                                viewModel.changeSortOrder(ReviewSortBy.RECENT)
                                showSortMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Most Helpful") },
                            onClick = {
                                viewModel.changeSortOrder(ReviewSortBy.HELPFUL)
                                showSortMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Highest Rating") },
                            onClick = {
                                viewModel.changeSortOrder(ReviewSortBy.RATING_HIGH)
                                showSortMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Lowest Rating") },
                            onClick = {
                                viewModel.changeSortOrder(ReviewSortBy.RATING_LOW)
                                showSortMenu = false
                            }
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onWriteReview(classId) },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Write review"
                )
            }
        }
    ) { paddingValues ->
        when {
            uiState.isLoading && uiState.reviews.isEmpty() -> {
                LoadingScreen(modifier = Modifier.padding(paddingValues))
            }
            uiState.error != null && uiState.reviews.isEmpty() -> {
                ErrorScreen(
                    message = uiState.error ?: "Unknown error",
                    onRetry = { viewModel.retry() },
                    modifier = Modifier.padding(paddingValues)
                )
            }
            uiState.reviews.isEmpty() -> {
                EmptyState(
                    message = "No reviews yet.\nBe the first to review this class!",
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
                    // Rating summary
                    if (uiState.ratingBreakdown != null) {
                        item {
                            RatingSummary(
                                averageRating = uiState.averageRating,
                                totalReviews = uiState.totalReviews,
                                ratingBreakdown = uiState.ratingBreakdown!!
                            )
                        }
                    }

                    // Reviews list
                    items(uiState.reviews, key = { it.id }) { review ->
                        ReviewItem(
                            review = review,
                            onHelpfulClick = { viewModel.markHelpful(review.id) }
                        )
                    }
                }
            }
        }
    }
}
