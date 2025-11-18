package com.numina.ui.social

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.numina.data.models.UserProfile
import com.numina.data.repository.Result
import com.numina.data.repository.SocialRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DiscoverUiState(
    val users: List<UserProfile> = emptyList(),
    val suggestedUsers: List<UserProfile> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val searchQuery: String = "",
    val selectedFitnessLevel: Int? = null,
    val selectedInterests: List<String> = emptyList(),
    val currentPage: Int = 1,
    val hasMore: Boolean = true,
    val isLoadingMore: Boolean = false
)

@HiltViewModel
class DiscoverViewModel @Inject constructor(
    private val socialRepository: SocialRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DiscoverUiState())
    val uiState: StateFlow<DiscoverUiState> = _uiState.asStateFlow()

    init {
        loadSuggestedUsers()
        discoverUsers()
    }

    private fun loadSuggestedUsers() {
        viewModelScope.launch {
            socialRepository.getSuggestedUsers().collect { result ->
                when (result) {
                    is Result.Success -> {
                        _uiState.update { it.copy(suggestedUsers = result.data) }
                    }
                    is Result.Error -> {
                        // Suggested users are optional, don't show error
                    }
                    is Result.Loading -> {
                        // No action needed
                    }
                }
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun updateFitnessLevel(level: Int?) {
        _uiState.update { it.copy(selectedFitnessLevel = level) }
    }

    fun toggleInterest(interest: String) {
        _uiState.update { state ->
            val interests = if (state.selectedInterests.contains(interest)) {
                state.selectedInterests - interest
            } else {
                state.selectedInterests + interest
            }
            state.copy(selectedInterests = interests)
        }
    }

    fun applyFilters() {
        _uiState.update { it.copy(currentPage = 1, users = emptyList()) }
        discoverUsers()
    }

    fun clearFilters() {
        _uiState.update {
            it.copy(
                searchQuery = "",
                selectedFitnessLevel = null,
                selectedInterests = emptyList(),
                currentPage = 1,
                users = emptyList()
            )
        }
        discoverUsers()
    }

    fun discoverUsers() {
        viewModelScope.launch {
            val state = _uiState.value
            _uiState.update { it.copy(isLoading = state.currentPage == 1, error = null) }

            socialRepository.discoverUsers(
                page = state.currentPage,
                searchQuery = state.searchQuery.takeIf { it.isNotBlank() },
                fitnessLevel = state.selectedFitnessLevel,
                interests = state.selectedInterests.takeIf { it.isNotEmpty() }
            ).collect { result ->
                when (result) {
                    is Result.Loading -> {
                        // Already handled
                    }
                    is Result.Success -> {
                        val newUsers = if (state.currentPage == 1) {
                            result.data.users
                        } else {
                            state.users + result.data.users
                        }

                        _uiState.update {
                            it.copy(
                                users = newUsers,
                                isLoading = false,
                                isRefreshing = false,
                                isLoadingMore = false,
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

    fun loadMoreUsers() {
        if (_uiState.value.isLoadingMore || !_uiState.value.hasMore) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingMore = true, currentPage = it.currentPage + 1) }
            discoverUsers()
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
