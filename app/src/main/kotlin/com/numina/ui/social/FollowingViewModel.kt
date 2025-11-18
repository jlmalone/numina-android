package com.numina.ui.social

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.numina.data.models.FollowUser
import com.numina.data.repository.Result
import com.numina.data.repository.SocialRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FollowingUiState(
    val followingList: List<FollowUser> = emptyList(),
    val followersList: List<FollowUser> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedTab: FollowTab = FollowTab.FOLLOWING
)

enum class FollowTab {
    FOLLOWING,
    FOLLOWERS
}

@HiltViewModel
class FollowingViewModel @Inject constructor(
    private val socialRepository: SocialRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FollowingUiState())
    val uiState: StateFlow<FollowingUiState> = _uiState.asStateFlow()

    init {
        loadCachedFollowing()
        loadFollowing()
        loadFollowers()
    }

    private fun loadCachedFollowing() {
        viewModelScope.launch {
            socialRepository.getCachedFollowing().collect { cachedFollowing ->
                if (cachedFollowing.isNotEmpty() && _uiState.value.followingList.isEmpty()) {
                    _uiState.update { it.copy(followingList = cachedFollowing) }
                }
            }
        }
    }

    fun loadFollowing() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            socialRepository.fetchFollowing().collect { result ->
                when (result) {
                    is Result.Loading -> {
                        // Already handled
                    }
                    is Result.Success -> {
                        _uiState.update {
                            it.copy(
                                followingList = result.data,
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

    fun loadFollowers() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            socialRepository.fetchFollowers().collect { result ->
                when (result) {
                    is Result.Loading -> {
                        // Already handled
                    }
                    is Result.Success -> {
                        _uiState.update {
                            it.copy(
                                followersList = result.data,
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

    fun selectTab(tab: FollowTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun unfollowUser(userId: String) {
        viewModelScope.launch {
            socialRepository.unfollowUser(userId).collect { result ->
                when (result) {
                    is Result.Success -> {
                        _uiState.update { state ->
                            state.copy(
                                followingList = state.followingList.map { user ->
                                    if (user.id == userId) {
                                        user.copy(isFollowing = false)
                                    } else {
                                        user
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

    fun followUser(userId: String) {
        viewModelScope.launch {
            socialRepository.followUser(userId).collect { result ->
                when (result) {
                    is Result.Success -> {
                        _uiState.update { state ->
                            state.copy(
                                followersList = state.followersList.map { user ->
                                    if (user.id == userId) {
                                        user.copy(isFollowing = true, isMutual = true)
                                    } else {
                                        user
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
