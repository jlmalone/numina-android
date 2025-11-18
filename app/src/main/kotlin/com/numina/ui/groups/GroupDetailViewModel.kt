package com.numina.ui.groups

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.numina.data.models.Group
import com.numina.data.models.GroupActivity
import com.numina.data.models.GroupMember
import com.numina.data.repository.GroupRepository
import com.numina.data.repository.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GroupDetailUiState(
    val group: Group? = null,
    val activities: List<GroupActivity> = emptyList(),
    val members: List<GroupMember> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isJoining: Boolean = false,
    val isLeaving: Boolean = false
)

@HiltViewModel
class GroupDetailViewModel @Inject constructor(
    private val groupRepository: GroupRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val groupId: String = checkNotNull(savedStateHandle["groupId"])

    private val _uiState = MutableStateFlow(GroupDetailUiState())
    val uiState: StateFlow<GroupDetailUiState> = _uiState.asStateFlow()

    init {
        loadGroupDetails()
        loadCachedGroup()
        loadCachedActivities()
    }

    private fun loadCachedGroup() {
        viewModelScope.launch {
            groupRepository.getGroupById(groupId).collect { group ->
                if (_uiState.value.group == null) {
                    _uiState.value = _uiState.value.copy(group = group)
                }
            }
        }
    }

    private fun loadCachedActivities() {
        viewModelScope.launch {
            groupRepository.getGroupActivities(groupId).collect { activities ->
                if (_uiState.value.activities.isEmpty()) {
                    _uiState.value = _uiState.value.copy(activities = activities)
                }
            }
        }
    }

    fun loadGroupDetails() {
        viewModelScope.launch {
            groupRepository.fetchGroupById(groupId).collect { result ->
                when (result) {
                    is Result.Loading -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = true,
                            error = null
                        )
                    }
                    is Result.Success -> {
                        _uiState.value = _uiState.value.copy(
                            group = result.data,
                            isLoading = false,
                            error = null
                        )
                        loadActivities()
                        loadMembers()
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

    private fun loadActivities() {
        viewModelScope.launch {
            groupRepository.fetchGroupActivities(groupId).collect { result ->
                when (result) {
                    is Result.Success -> {
                        _uiState.value = _uiState.value.copy(activities = result.data.activities)
                    }
                    is Result.Error -> {
                        // Keep cached activities, just log error
                    }
                    is Result.Loading -> {}
                }
            }
        }
    }

    private fun loadMembers() {
        viewModelScope.launch {
            groupRepository.fetchGroupMembers(groupId).collect { result ->
                when (result) {
                    is Result.Success -> {
                        _uiState.value = _uiState.value.copy(members = result.data.members)
                    }
                    is Result.Error -> {
                        // Silently fail for members
                    }
                    is Result.Loading -> {}
                }
            }
        }
    }

    fun joinGroup() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isJoining = true)
            groupRepository.joinGroup(groupId).collect { result ->
                when (result) {
                    is Result.Success -> {
                        _uiState.value = _uiState.value.copy(
                            group = result.data,
                            isJoining = false
                        )
                    }
                    is Result.Error -> {
                        _uiState.value = _uiState.value.copy(
                            error = result.message,
                            isJoining = false
                        )
                    }
                    is Result.Loading -> {}
                }
            }
        }
    }

    fun leaveGroup() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLeaving = true)
            groupRepository.leaveGroup(groupId).collect { result ->
                when (result) {
                    is Result.Success -> {
                        _uiState.value = _uiState.value.copy(
                            group = result.data,
                            isLeaving = false
                        )
                    }
                    is Result.Error -> {
                        _uiState.value = _uiState.value.copy(
                            error = result.message,
                            isLeaving = false
                        )
                    }
                    is Result.Loading -> {}
                }
            }
        }
    }

    fun rsvpToActivity(activityId: String, status: String) {
        viewModelScope.launch {
            groupRepository.rsvpToActivity(groupId, activityId, status).collect { result ->
                when (result) {
                    is Result.Success -> {
                        loadActivities()
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
