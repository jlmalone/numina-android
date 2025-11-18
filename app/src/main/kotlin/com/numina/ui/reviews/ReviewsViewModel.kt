package com.numina.ui.reviews

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.numina.data.models.Review
import com.numina.data.models.ReviewSortBy
import com.numina.data.models.ReviewsListResponse
import com.numina.data.repository.Result
import com.numina.data.repository.ReviewRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ReviewsUiState(
    val isLoading: Boolean = false,
    val reviews: List<Review> = emptyList(),
    val averageRating: Double = 0.0,
    val totalReviews: Int = 0,
    val ratingBreakdown: com.numina.data.models.RatingBreakdown? = null,
    val error: String? = null,
    val sortBy: ReviewSortBy = ReviewSortBy.RECENT
)

@HiltViewModel
class ReviewsViewModel @Inject constructor(
    private val reviewRepository: ReviewRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReviewsUiState())
    val uiState: StateFlow<ReviewsUiState> = _uiState.asStateFlow()

    private var currentClassId: String? = null

    fun loadReviews(classId: String, sortBy: ReviewSortBy = ReviewSortBy.RECENT) {
        currentClassId = classId
        _uiState.update { it.copy(sortBy = sortBy) }

        viewModelScope.launch {
            // First load from cache
            reviewRepository.getCachedReviewsForClass(classId)
                .collect { cachedReviews ->
                    if (cachedReviews.isNotEmpty() && !_uiState.value.isLoading) {
                        _uiState.update { it.copy(reviews = cachedReviews) }
                    }
                }
        }

        viewModelScope.launch {
            reviewRepository.fetchReviews(classId, sortBy = sortBy).collect { result ->
                when (result) {
                    is Result.Loading -> {
                        _uiState.update { it.copy(isLoading = true, error = null) }
                    }
                    is Result.Success -> {
                        val response = result.data
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                reviews = response.reviews,
                                averageRating = response.averageRating,
                                totalReviews = response.total,
                                ratingBreakdown = response.ratingBreakdown,
                                error = null
                            )
                        }
                    }
                    is Result.Error -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = result.message
                            )
                        }
                    }
                }
            }
        }
    }

    fun changeSortOrder(sortBy: ReviewSortBy) {
        currentClassId?.let { classId ->
            loadReviews(classId, sortBy)
        }
    }

    fun markHelpful(reviewId: String) {
        viewModelScope.launch {
            reviewRepository.markHelpful(reviewId).collect { result ->
                when (result) {
                    is Result.Success -> {
                        // Update the review in the list
                        _uiState.update { state ->
                            state.copy(
                                reviews = state.reviews.map { review ->
                                    if (review.id == reviewId) {
                                        review.copy(
                                            isHelpful = result.data.isHelpful,
                                            helpfulCount = result.data.helpfulCount
                                        )
                                    } else {
                                        review
                                    }
                                }
                            )
                        }
                    }
                    is Result.Error -> {
                        // Optionally show error
                    }
                    is Result.Loading -> {
                        // Do nothing
                    }
                }
            }
        }
    }

    fun retry() {
        currentClassId?.let { classId ->
            loadReviews(classId, _uiState.value.sortBy)
        }
    }
}
