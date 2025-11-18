package com.numina.ui.reviews

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.numina.data.models.Review
import com.numina.ui.components.NuminaButton
import com.numina.ui.reviews.components.ReviewForm

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WriteReviewScreen(
    classId: String?,
    existingReview: Review? = null,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: WriteReviewViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(classId, existingReview) {
        when {
            existingReview != null -> viewModel.initForEdit(existingReview)
            classId != null -> viewModel.initForClass(classId)
        }
    }

    LaunchedEffect(uiState.success) {
        if (uiState.success) {
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (uiState.isEditMode) "Edit Review" else "Write Review"
                    )
                },
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
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ReviewForm(
                rating = uiState.rating,
                onRatingChange = viewModel::updateRating,
                title = uiState.title,
                onTitleChange = viewModel::updateTitle,
                content = uiState.content,
                onContentChange = viewModel::updateContent,
                pros = uiState.pros,
                onProsChange = viewModel::updatePros,
                cons = uiState.cons,
                onConsChange = viewModel::updateCons,
                modifier = Modifier.weight(1f)
            )

            // Error message
            if (uiState.error != null) {
                Text(
                    text = uiState.error ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }

            // Submit button
            NuminaButton(
                text = if (uiState.isEditMode) "Update Review" else "Post Review",
                onClick = { viewModel.submitReview() },
                enabled = !uiState.isLoading && uiState.rating > 0 && uiState.content.isNotBlank(),
                loading = uiState.isLoading
            )
        }
    }
}
