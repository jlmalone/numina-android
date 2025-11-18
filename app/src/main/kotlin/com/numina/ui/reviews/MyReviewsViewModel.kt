package com.numina.ui.reviews

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.numina.data.models.PendingReview
import com.numina.data.models.ReviewWithClass
import com.numina.data.repository.Result
import com.numina.data.repository.ReviewRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MyReviewsUiState(
    val isLoading: Boolean = false,
    val reviews: List<ReviewWithClass> = emptyList(),
    val error: String? = null,
    val deleteInProgress: String? = null
)

data class PendingReviewsUiState(
    val isLoading: Boolean = false,
    val pendingReviews: List<PendingReview> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class MyReviewsViewModel @Inject constructor(
    private val reviewRepository: ReviewRepository
) : ViewModel() {

    private val _myReviewsState = MutableStateFlow(MyReviewsUiState())
    val myReviewsState: StateFlow<MyReviewsUiState> = _myReviewsState.asStateFlow()

    private val _pendingReviewsState = MutableStateFlow(PendingReviewsUiState())
    val pendingReviewsState: StateFlow<PendingReviewsUiState> = _pendingReviewsState.asStateFlow()

    init {
        loadMyReviews()
        loadPendingReviews()
    }

    fun loadMyReviews() {
        viewModelScope.launch {
            reviewRepository.getMyReviews().collect { result ->
                when (result) {
                    is Result.Loading -> {
                        _myReviewsState.update { it.copy(isLoading = true, error = null) }
                    }
                    is Result.Success -> {
                        _myReviewsState.update {
                            it.copy(
                                isLoading = false,
                                reviews = result.data.reviews,
                                error = null
                            )
                        }
                    }
                    is Result.Error -> {
                        _myReviewsState.update {
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

    fun loadPendingReviews() {
        viewModelScope.launch {
            reviewRepository.getPendingReviews().collect { result ->
                when (result) {
                    is Result.Loading -> {
                        _pendingReviewsState.update { it.copy(isLoading = true, error = null) }
                    }
                    is Result.Success -> {
                        _pendingReviewsState.update {
                            it.copy(
                                isLoading = false,
                                pendingReviews = result.data.pendingReviews,
                                error = null
                            )
                        }
                    }
                    is Result.Error -> {
                        _pendingReviewsState.update {
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

    fun deleteReview(reviewId: String) {
        viewModelScope.launch {
            _myReviewsState.update { it.copy(deleteInProgress = reviewId) }
            reviewRepository.deleteReview(reviewId).collect { result ->
                when (result) {
                    is Result.Success -> {
                        _myReviewsState.update {
                            it.copy(
                                reviews = it.reviews.filter { review -> review.review.id != reviewId },
                                deleteInProgress = null
                            )
                        }
                    }
                    is Result.Error -> {
                        _myReviewsState.update {
                            it.copy(
                                error = result.message,
                                deleteInProgress = null
                            )
                        }
                    }
                    is Result.Loading -> {
                        // Keep deleteInProgress set
                    }
                }
            }
        }
    }

    fun retryMyReviews() {
        loadMyReviews()
    }

    fun retryPendingReviews() {
        loadPendingReviews()
    }
}
