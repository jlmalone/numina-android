package com.numina.ui.social

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.numina.data.models.Activity
import com.numina.data.models.UserProfile
import com.numina.data.repository.Result
import com.numina.data.repository.SocialRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UserProfileUiState(
    val profile: UserProfile? = null,
    val activities: List<Activity> = emptyList(),
    val isLoading: Boolean = false,
    val isFollowing: Boolean = false,
    val error: String? = null,
    val currentPage: Int = 1,
    val hasMore: Boolean = true,
    val isLoadingMore: Boolean = false
)

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val socialRepository: SocialRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val userId: String = checkNotNull(savedStateHandle["userId"])

    private val _uiState = MutableStateFlow(UserProfileUiState())
    val uiState: StateFlow<UserProfileUiState> = _uiState.asStateFlow()

    init {
        loadCachedProfile()
        loadProfile()
        loadUserActivities()
    }

    private fun loadCachedProfile() {
        viewModelScope.launch {
            socialRepository.getCachedUserProfile(userId).collect { cachedProfile ->
                if (cachedProfile != null && _uiState.value.profile == null) {
                    _uiState.update {
                        it.copy(
                            profile = cachedProfile,
                            isFollowing = cachedProfile.isFollowing
                        )
                    }
                }
            }
        }
    }

    fun loadProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            socialRepository.fetchUserProfile(userId).collect { result ->
                when (result) {
                    is Result.Loading -> {
                        // Already handled
                    }
                    is Result.Success -> {
                        _uiState.update {
                            it.copy(
                                profile = result.data,
                                isFollowing = result.data.isFollowing,
                                isLoading = false,
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

    private fun loadUserActivities() {
        viewModelScope.launch {
            val currentPage = _uiState.value.currentPage

            socialRepository.fetchUserActivities(userId, currentPage).collect { result ->
                when (result) {
                    is Result.Loading -> {
                        // No action needed
                    }
                    is Result.Success -> {
                        _uiState.update {
                            it.copy(
                                activities = result.data.activities,
                                hasMore = result.data.hasMore,
                                isLoadingMore = false
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

    fun loadMoreActivities() {
        if (_uiState.value.isLoadingMore || !_uiState.value.hasMore) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingMore = true) }

            val nextPage = _uiState.value.currentPage + 1
            socialRepository.fetchUserActivities(userId, nextPage).collect { result ->
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

    fun toggleFollow() {
        viewModelScope.launch {
            val isCurrentlyFollowing = _uiState.value.isFollowing

            if (isCurrentlyFollowing) {
                socialRepository.unfollowUser(userId).collect { result ->
                    when (result) {
                        is Result.Success -> {
                            _uiState.update {
                                it.copy(
                                    isFollowing = false,
                                    profile = it.profile?.copy(
                                        isFollowing = false,
                                        followersCount = maxOf(0, (it.profile.followersCount - 1))
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
            } else {
                socialRepository.followUser(userId).collect { result ->
                    when (result) {
                        is Result.Success -> {
                            _uiState.update {
                                it.copy(
                                    isFollowing = true,
                                    profile = it.profile?.copy(
                                        isFollowing = true,
                                        followersCount = (it.profile.followersCount + 1)
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
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
