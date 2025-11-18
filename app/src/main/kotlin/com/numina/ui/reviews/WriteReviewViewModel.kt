package com.numina.ui.reviews

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.numina.data.db.ReviewDraftEntity
import com.numina.data.models.CreateReviewRequest
import com.numina.data.models.Review
import com.numina.data.models.UpdateReviewRequest
import com.numina.data.repository.Result
import com.numina.data.repository.ReviewRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

data class WriteReviewUiState(
    val isLoading: Boolean = false,
    val rating: Int = 0,
    val title: String = "",
    val content: String = "",
    val pros: List<String> = emptyList(),
    val cons: List<String> = emptyList(),
    val photoUrls: List<String> = emptyList(),
    val isUploading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false,
    val isEditMode: Boolean = false,
    val reviewId: String? = null
)

@HiltViewModel
class WriteReviewViewModel @Inject constructor(
    private val reviewRepository: ReviewRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WriteReviewUiState())
    val uiState: StateFlow<WriteReviewUiState> = _uiState.asStateFlow()

    private var currentClassId: String? = null

    fun initForClass(classId: String) {
        currentClassId = classId
        loadDraft(classId)
    }

    fun initForEdit(review: Review) {
        _uiState.update {
            it.copy(
                isEditMode = true,
                reviewId = review.id,
                rating = review.rating,
                title = review.title ?: "",
                content = review.content,
                pros = review.pros ?: emptyList(),
                cons = review.cons ?: emptyList(),
                photoUrls = review.photos ?: emptyList()
            )
        }
        currentClassId = review.classId
    }

    private fun loadDraft(classId: String) {
        viewModelScope.launch {
            reviewRepository.getDraft(classId).collect { draft ->
                if (draft != null && !_uiState.value.isEditMode) {
                    _uiState.update {
                        it.copy(
                            rating = draft.rating,
                            title = draft.title ?: "",
                            content = draft.content,
                            pros = draft.pros ?: emptyList(),
                            cons = draft.cons ?: emptyList(),
                            photoUrls = draft.photos ?: emptyList()
                        )
                    }
                }
            }
        }
    }

    fun updateRating(rating: Int) {
        _uiState.update { it.copy(rating = rating) }
        saveDraft()
    }

    fun updateTitle(title: String) {
        _uiState.update { it.copy(title = title) }
        saveDraft()
    }

    fun updateContent(content: String) {
        _uiState.update { it.copy(content = content) }
        saveDraft()
    }

    fun updatePros(pros: List<String>) {
        _uiState.update { it.copy(pros = pros) }
        saveDraft()
    }

    fun updateCons(cons: List<String>) {
        _uiState.update { it.copy(cons = cons) }
        saveDraft()
    }

    fun uploadPhoto(file: File) {
        viewModelScope.launch {
            _uiState.update { it.copy(isUploading = true, error = null) }
            reviewRepository.uploadPhoto(file).collect { result ->
                when (result) {
                    is Result.Success -> {
                        _uiState.update {
                            it.copy(
                                isUploading = false,
                                photoUrls = it.photoUrls + result.data,
                                error = null
                            )
                        }
                        saveDraft()
                    }
                    is Result.Error -> {
                        _uiState.update {
                            it.copy(
                                isUploading = false,
                                error = result.message
                            )
                        }
                    }
                    is Result.Loading -> {
                        _uiState.update { it.copy(isUploading = true) }
                    }
                }
            }
        }
    }

    fun removePhoto(photoUrl: String) {
        _uiState.update {
            it.copy(photoUrls = it.photoUrls.filter { url -> url != photoUrl })
        }
        saveDraft()
    }

    private fun saveDraft() {
        val classId = currentClassId ?: return
        if (_uiState.value.isEditMode) return // Don't save drafts in edit mode

        viewModelScope.launch {
            val state = _uiState.value
            val draft = ReviewDraftEntity(
                classId = classId,
                rating = state.rating,
                title = state.title.takeIf { it.isNotBlank() },
                content = state.content,
                pros = state.pros.takeIf { it.isNotEmpty() },
                cons = state.cons.takeIf { it.isNotEmpty() },
                photos = state.photoUrls.takeIf { it.isNotEmpty() },
                updatedAt = System.currentTimeMillis()
            )
            reviewRepository.saveDraft(draft)
        }
    }

    fun submitReview() {
        val classId = currentClassId ?: return
        val state = _uiState.value

        if (state.rating == 0 || state.content.isBlank()) {
            _uiState.update { it.copy(error = "Please provide a rating and review content") }
            return
        }

        viewModelScope.launch {
            if (state.isEditMode && state.reviewId != null) {
                updateReview(state.reviewId)
            } else {
                createReview(classId)
            }
        }
    }

    private suspend fun createReview(classId: String) {
        val state = _uiState.value
        val request = CreateReviewRequest(
            rating = state.rating,
            title = state.title.takeIf { it.isNotBlank() },
            content = state.content,
            pros = state.pros.takeIf { it.isNotEmpty() },
            cons = state.cons.takeIf { it.isNotEmpty() },
            photos = state.photoUrls.takeIf { it.isNotEmpty() }
        )

        reviewRepository.createReview(classId, request).collect { result ->
            when (result) {
                is Result.Loading -> {
                    _uiState.update { it.copy(isLoading = true, error = null) }
                }
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            success = true,
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

    private suspend fun updateReview(reviewId: String) {
        val state = _uiState.value
        val request = UpdateReviewRequest(
            rating = state.rating,
            title = state.title.takeIf { it.isNotBlank() },
            content = state.content,
            pros = state.pros.takeIf { it.isNotEmpty() },
            cons = state.cons.takeIf { it.isNotEmpty() },
            photos = state.photoUrls.takeIf { it.isNotEmpty() }
        )

        reviewRepository.updateReview(reviewId, request).collect { result ->
            when (result) {
                is Result.Loading -> {
                    _uiState.update { it.copy(isLoading = true, error = null) }
                }
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            success = true,
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

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
