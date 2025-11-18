package com.numina.ui.messages

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.numina.data.models.Conversation
import com.numina.data.repository.MessagingRepository
import com.numina.data.repository.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MessagesUiState(
    val conversations: List<Conversation> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val totalUnreadCount: Int = 0
)

@HiltViewModel
class MessagesViewModel @Inject constructor(
    private val messagingRepository: MessagingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MessagesUiState(isLoading = true))
    val uiState: StateFlow<MessagesUiState> = _uiState.asStateFlow()

    init {
        loadConversations()
        observeConversations()
        observeUnreadCount()
    }

    private fun observeConversations() {
        viewModelScope.launch {
            messagingRepository.getConversations()
                .catch { e ->
                    _uiState.update { it.copy(error = e.message, isLoading = false) }
                }
                .collect { conversations ->
                    _uiState.update {
                        it.copy(
                            conversations = conversations,
                            isLoading = false,
                            isRefreshing = false
                        )
                    }
                }
        }
    }

    private fun observeUnreadCount() {
        viewModelScope.launch {
            messagingRepository.getTotalUnreadCount()
                .collect { count ->
                    _uiState.update { it.copy(totalUnreadCount = count) }
                }
        }
    }

    fun loadConversations(refresh: Boolean = false) {
        viewModelScope.launch {
            if (refresh) {
                _uiState.update { it.copy(isRefreshing = true, error = null) }
            } else {
                _uiState.update { it.copy(isLoading = true, error = null) }
            }

            when (val result = messagingRepository.syncConversations()) {
                is Result.Success -> {
                    // Conversations will be updated via Flow
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            error = result.message,
                            isLoading = false,
                            isRefreshing = false
                        )
                    }
                }
                is Result.Loading -> {}
            }
        }
    }

    fun deleteConversation(conversationId: String) {
        viewModelScope.launch {
            when (messagingRepository.deleteConversation(conversationId)) {
                is Result.Success -> {
                    // Conversation will be removed via Flow
                }
                is Result.Error -> {
                    // Handle error
                }
                is Result.Loading -> {}
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
