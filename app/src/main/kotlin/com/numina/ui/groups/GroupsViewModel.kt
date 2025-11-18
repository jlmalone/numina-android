package com.numina.ui.groups

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.numina.data.models.Group
import com.numina.data.repository.GroupRepository
import com.numina.data.repository.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GroupFilters(
    val category: String? = null,
    val location: String? = null,
    val search: String? = null
)

data class GroupsUiState(
    val groups: List<Group> = emptyList(),
    val myGroups: List<Group> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val filters: GroupFilters = GroupFilters(),
    val showMyGroupsOnly: Boolean = false
)

@HiltViewModel
class GroupsViewModel @Inject constructor(
    private val groupRepository: GroupRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GroupsUiState())
    val uiState: StateFlow<GroupsUiState> = _uiState.asStateFlow()

    init {
        loadGroups()
        loadCachedGroups()
        loadMyGroups()
    }

    private fun loadCachedGroups() {
        viewModelScope.launch {
            groupRepository.getCachedGroups().collect { cached ->
                if (_uiState.value.groups.isEmpty()) {
                    _uiState.value = _uiState.value.copy(groups = cached)
                }
            }
        }
    }

    private fun loadMyGroups() {
        viewModelScope.launch {
            groupRepository.getMyGroups().collect { myGroups ->
                _uiState.value = _uiState.value.copy(myGroups = myGroups)
            }
        }
    }

    fun loadGroups(refresh: Boolean = false) {
        viewModelScope.launch {
            val filters = _uiState.value.filters
            groupRepository.fetchGroups(
                category = filters.category,
                location = filters.location,
                search = filters.search
            ).collect { result ->
                when (result) {
                    is Result.Loading -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = true,
                            error = null
                        )
                    }
                    is Result.Success -> {
                        _uiState.value = _uiState.value.copy(
                            groups = result.data.groups,
                            isLoading = false,
                            error = null
                        )
                    }
                    is Result.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = result.message
                        )
                    }
                }
            }
        }
    }

    fun applyFilters(filters: GroupFilters) {
        _uiState.value = _uiState.value.copy(filters = filters)
        loadGroups()
    }

    fun clearFilters() {
        _uiState.value = _uiState.value.copy(filters = GroupFilters())
        loadGroups()
    }

    fun toggleShowMyGroups(showMyGroupsOnly: Boolean) {
        _uiState.value = _uiState.value.copy(showMyGroupsOnly = showMyGroupsOnly)
        if (showMyGroupsOnly) {
            refreshMyGroups()
        } else {
            loadGroups()
        }
    }

    fun refreshMyGroups() {
        viewModelScope.launch {
            groupRepository.fetchMyGroups().collect { result ->
                when (result) {
                    is Result.Loading -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = true,
                            error = null
                        )
                    }
                    is Result.Success -> {
                        _uiState.value = _uiState.value.copy(
                            myGroups = result.data.groups,
                            isLoading = false,
                            error = null
                        )
                    }
                    is Result.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = result.message
                        )
                    }
                }
            }
        }
    }

    fun joinGroup(groupId: String) {
        viewModelScope.launch {
            groupRepository.joinGroup(groupId).collect { result ->
                when (result) {
                    is Result.Success -> {
                        loadGroups()
                        refreshMyGroups()
                    }
                    is Result.Error -> {
                        _uiState.value = _uiState.value.copy(error = result.message)
                    }
                    is Result.Loading -> {}
                }
            }
        }
    }
}
