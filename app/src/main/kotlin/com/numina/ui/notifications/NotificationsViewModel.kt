package com.numina.ui.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.numina.data.models.Notification
import com.numina.data.repository.NotificationsRepository
import com.numina.data.repository.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NotificationsUiState(
    val notifications: List<Notification> = emptyList(),
    val unreadCount: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val notificationsRepository: NotificationsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationsUiState())
    val uiState: StateFlow<NotificationsUiState> = _uiState.asStateFlow()

    init {
        loadNotifications()
        loadCachedNotifications()
        loadUnreadCount()
    }

    private fun loadCachedNotifications() {
        viewModelScope.launch {
            notificationsRepository.getCachedNotifications().collect { cached ->
                if (_uiState.value.notifications.isEmpty()) {
                    _uiState.value = _uiState.value.copy(notifications = cached)
                }
            }
        }
    }

    private fun loadUnreadCount() {
        viewModelScope.launch {
            notificationsRepository.getUnreadCount().collect { count ->
                _uiState.value = _uiState.value.copy(unreadCount = count)
            }
        }
    }

    fun loadNotifications(refresh: Boolean = false) {
        viewModelScope.launch {
            notificationsRepository.fetchNotifications().collect { result ->
                when (result) {
                    is Result.Loading -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = true,
                            error = null
                        )
                    }
                    is Result.Success -> {
                        _uiState.value = _uiState.value.copy(
                            notifications = result.data,
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

    fun markAsRead(notificationId: String) {
        viewModelScope.launch {
            notificationsRepository.markAsRead(notificationId).collect { result ->
                when (result) {
                    is Result.Success -> {
                        // Refresh notifications to update UI
                        loadNotifications()
                    }
                    is Result.Error -> {
                        _uiState.value = _uiState.value.copy(error = result.message)
                    }
                    else -> {}
                }
            }
        }
    }

    fun markAllAsRead() {
        viewModelScope.launch {
            notificationsRepository.markAllAsRead().collect { result ->
                when (result) {
                    is Result.Success -> {
                        loadNotifications()
                    }
                    is Result.Error -> {
                        _uiState.value = _uiState.value.copy(error = result.message)
                    }
                    else -> {}
                }
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
