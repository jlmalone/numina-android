package com.numina.ui.social

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.numina.data.models.Activity
import com.numina.data.repository.Result
import com.numina.data.repository.SocialRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FeedUiState(
    val activities: List<Activity> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val currentPage: Int = 1,
    val hasMore: Boolean = true,
    val isLoadingMore: Boolean = false
)

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val socialRepository: SocialRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FeedUiState())
    val uiState: StateFlow<FeedUiState> = _uiState.asStateFlow()

    init {
        loadCachedActivities()
        loadFeed(refresh = false)
    }

    private fun loadCachedActivities() {
        viewModelScope.launch {
            socialRepository.getCachedActivities().collect { cachedActivities ->
                if (cachedActivities.isNotEmpty() && _uiState.value.activities.isEmpty()) {
                    _uiState.update { it.copy(activities = cachedActivities) }
                }
            }
        }
    }

    fun loadFeed(refresh: Boolean = false) {
        viewModelScope.launch {
            val currentPage = if (refresh) 1 else _uiState.value.currentPage

            _uiState.update {
                it.copy(
                    isLoading = currentPage == 1 && !refresh,
                    isRefreshing = refresh,
                    error = null
                )
            }

            socialRepository.fetchActivityFeed(currentPage, refresh).collect { result ->
                when (result) {
                    is Result.Loading -> {
                        // Already handled above
                    }
                    is Result.Success -> {
                        val newActivities = if (refresh || currentPage == 1) {
                            result.data.activities
                        } else {
                            _uiState.value.activities + result.data.activities
                        }

                        _uiState.update {
                            it.copy(
                                activities = newActivities,
                                isLoading = false,
                                isRefreshing = false,
                                isLoadingMore = false,
                                currentPage = currentPage,
                                hasMore = result.data.hasMore,
                                error = null
                            )
                        }
                    }
                    is Result.Error -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isRefreshing = false,
                                isLoadingMore = false,
                                error = result.message
                            )
                        }
                    }
                }
            }
        }
    }

    fun loadMoreActivities() {
        if (_uiState.value.isLoadingMore || !_uiState.value.hasMore) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingMore = true) }

            val nextPage = _uiState.value.currentPage + 1
            socialRepository.fetchActivityFeed(nextPage, false).collect { result ->
                when (result) {
                    is Result.Loading -> {
                        // Already handled
                    }
                    is Result.Success -> {
                        _uiState.update {
                            it.copy(
                                activities = it.activities + result.data.activities,
                                isLoadingMore = false,
                                currentPage = nextPage,
                                hasMore = result.data.hasMore
                            )
                        }
                    }
                    is Result.Error -> {
                        _uiState.update {
                            it.copy(
                                isLoadingMore = false,
                                error = result.message
                            )
                        }
                    }
                }
            }
        }
    }

    fun likeActivity(activityId: String) {
        viewModelScope.launch {
            socialRepository.likeActivity(activityId).collect { result ->
                when (result) {
                    is Result.Success -> {
                        _uiState.update { state ->
                            state.copy(
                                activities = state.activities.map { activity ->
                                    if (activity.id == activityId) {
                                        activity.copy(
                                            isLiked = true,
                                            likesCount = activity.likesCount + 1
                                        )
                                    } else {
                                        activity
                                    }
                                }
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

    fun unlikeActivity(activityId: String) {
        viewModelScope.launch {
            socialRepository.unlikeActivity(activityId).collect { result ->
                when (result) {
                    is Result.Success -> {
                        _uiState.update { state ->
                            state.copy(
                                activities = state.activities.map { activity ->
                                    if (activity.id == activityId) {
                                        activity.copy(
                                            isLiked = false,
                                            likesCount = maxOf(0, activity.likesCount - 1)
                                        )
                                    } else {
                                        activity
                                    }
                                }
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
