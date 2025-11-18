package com.numina.ui.social

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.numina.data.models.Activity
import com.numina.data.models.Comment
import com.numina.data.repository.Result
import com.numina.data.repository.SocialRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ActivityDetailUiState(
    val activity: Activity? = null,
    val comments: List<Comment> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingComments: Boolean = false,
    val isPostingComment: Boolean = false,
    val error: String? = null,
    val commentText: String = ""
)

@HiltViewModel
class ActivityDetailViewModel @Inject constructor(
    private val socialRepository: SocialRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val activityId: String = checkNotNull(savedStateHandle["activityId"])

    private val _uiState = MutableStateFlow(ActivityDetailUiState())
    val uiState: StateFlow<ActivityDetailUiState> = _uiState.asStateFlow()

    init {
        loadCachedComments()
        loadComments()
    }

    private fun loadCachedComments() {
        viewModelScope.launch {
            socialRepository.getCachedComments(activityId).collect { cachedComments ->
                if (cachedComments.isNotEmpty() && _uiState.value.comments.isEmpty()) {
                    _uiState.update { it.copy(comments = cachedComments) }
                }
            }
        }
    }

    fun setActivity(activity: Activity) {
        _uiState.update { it.copy(activity = activity) }
    }

    fun loadComments() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingComments = true, error = null) }

            socialRepository.fetchComments(activityId).collect { result ->
                when (result) {
                    is Result.Loading -> {
                        // Already handled
                    }
                    is Result.Success -> {
                        _uiState.update {
                            it.copy(
                                comments = result.data,
                                isLoadingComments = false,
                                error = null
                            )
                        }
                    }
                    is Result.Error -> {
                        _uiState.update {
                            it.copy(
                                isLoadingComments = false,
                                error = result.message
                            )
                        }
                    }
                }
            }
        }
    }

    fun updateCommentText(text: String) {
        _uiState.update { it.copy(commentText = text) }
    }

    fun postComment() {
        val text = _uiState.value.commentText.trim()
        if (text.isEmpty()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isPostingComment = true, error = null) }

            socialRepository.addComment(activityId, text).collect { result ->
                when (result) {
                    is Result.Loading -> {
                        // Already handled
                    }
                    is Result.Success -> {
                        _uiState.update {
                            it.copy(
                                comments = it.comments + result.data,
                                commentText = "",
                                isPostingComment = false,
                                activity = it.activity?.copy(
                                    commentsCount = it.activity.commentsCount + 1
                                ),
                                error = null
                            )
                        }
                    }
                    is Result.Error -> {
                        _uiState.update {
                            it.copy(
                                isPostingComment = false,
                                error = result.message
                            )
                        }
                    }
                }
            }
        }
    }

    fun likeActivity() {
        val currentActivity = _uiState.value.activity ?: return

        viewModelScope.launch {
            socialRepository.likeActivity(activityId).collect { result ->
                when (result) {
                    is Result.Success -> {
                        _uiState.update {
                            it.copy(
                                activity = currentActivity.copy(
                                    isLiked = true,
                                    likesCount = currentActivity.likesCount + 1
                                )
                            )
                        }
                    }
                    is Result.Error -> {
                        _uiState.update { it.copy(error = result.message) }
                    }
                    is Result.Loading -> {
                        // No action needed
                    }
                }
            }
        }
    }

    fun unlikeActivity() {
        val currentActivity = _uiState.value.activity ?: return

        viewModelScope.launch {
            socialRepository.unlikeActivity(activityId).collect { result ->
                when (result) {
                    is Result.Success -> {
                        _uiState.update {
                            it.copy(
                                activity = currentActivity.copy(
                                    isLiked = false,
                                    likesCount = maxOf(0, currentActivity.likesCount - 1)
                                )
                            )
                        }
                    }
                    is Result.Error -> {
                        _uiState.update { it.copy(error = result.message) }
                    }
                    is Result.Loading -> {
                        // No action needed
                    }
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
