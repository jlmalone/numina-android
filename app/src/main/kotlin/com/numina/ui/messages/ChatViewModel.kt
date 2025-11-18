package com.numina.ui.messages

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.numina.data.models.Message
import com.numina.data.models.WebSocketMessage
import com.numina.data.repository.MessagingRepository
import com.numina.data.repository.Result
import com.numina.data.repository.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChatUiState(
    val messages: List<Message> = emptyList(),
    val isLoading: Boolean = false,
    val isSending: Boolean = false,
    val error: String? = null,
    val participantName: String = "",
    val participantAvatar: String? = null,
    val isTyping: Boolean = false,
    val conversationId: String = "",
    val participantId: String = ""
)

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val messagingRepository: MessagingRepository,
    private val tokenManager: TokenManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val conversationId: String = savedStateHandle.get<String>("conversationId") ?: ""
    private val participantId: String = savedStateHandle.get<String>("participantId") ?: ""
    private val participantName: String = savedStateHandle.get<String>("participantName") ?: ""
    private val participantAvatar: String? = savedStateHandle.get<String>("participantAvatar")

    private val _uiState = MutableStateFlow(
        ChatUiState(
            isLoading = true,
            conversationId = conversationId,
            participantId = participantId,
            participantName = participantName,
            participantAvatar = participantAvatar
        )
    )
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    private var typingJob: Job? = null
    private var webSocketJob: Job? = null

    init {
        loadMessages()
        observeMessages()
        connectWebSocket()
        markConversationAsRead()
    }

    private fun observeMessages() {
        viewModelScope.launch {
            messagingRepository.getMessagesForConversation(conversationId)
                .catch { e ->
                    _uiState.update { it.copy(error = e.message, isLoading = false) }
                }
                .collect { messages ->
                    _uiState.update {
                        it.copy(
                            messages = messages,
                            isLoading = false
                        )
                    }
                }
        }
    }

    private fun loadMessages() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            when (messagingRepository.syncMessages(conversationId)) {
                is Result.Success -> {
                    // Messages will be updated via Flow
                }
                is Result.Error -> {
                    _uiState.update { it.copy(isLoading = false) }
                }
                is Result.Loading -> {}
            }
        }
    }

    private fun connectWebSocket() {
        webSocketJob = viewModelScope.launch {
            messagingRepository.connectWebSocket()
                .catch { e ->
                    // Handle WebSocket errors
                }
                .collect { message ->
                    handleWebSocketMessage(message)
                }
        }
    }

    private suspend fun handleWebSocketMessage(message: WebSocketMessage) {
        when (message) {
            is WebSocketMessage.NewMessage -> {
                // Only handle messages for this conversation
                if (message.message.conversationId == conversationId) {
                    messagingRepository.handleWebSocketMessage(message)
                    // Mark as read since user is viewing the conversation
                    markConversationAsRead()
                }
            }
            is WebSocketMessage.TypingIndicator -> {
                if (message.conversationId == conversationId && message.userId == participantId) {
                    _uiState.update { it.copy(isTyping = message.isTyping) }
                }
            }
            is WebSocketMessage.MessageRead -> {
                messagingRepository.handleWebSocketMessage(message)
            }
        }
    }

    fun sendMessage(content: String) {
        if (content.isBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSending = true, error = null) }

            val currentUserId = tokenManager.getUserId() ?: ""

            when (val result = messagingRepository.sendMessage(
                conversationId = conversationId,
                receiverId = participantId,
                content = content.trim(),
                currentUserId = currentUserId
            )) {
                is Result.Success -> {
                    _uiState.update { it.copy(isSending = false) }
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            isSending = false,
                            error = result.message
                        )
                    }
                }
                is Result.Loading -> {}
            }
        }
    }

    fun onTyping() {
        // Cancel previous typing job
        typingJob?.cancel()

        // Send typing indicator
        messagingRepository.sendTypingIndicator(conversationId, true)

        // Auto-stop typing after 3 seconds
        typingJob = viewModelScope.launch {
            delay(3000)
            messagingRepository.sendTypingIndicator(conversationId, false)
        }
    }

    private fun markConversationAsRead() {
        viewModelScope.launch {
            messagingRepository.markConversationAsRead(conversationId)
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    override fun onCleared() {
        super.onCleared()
        messagingRepository.sendTypingIndicator(conversationId, false)
        webSocketJob?.cancel()
    }
}
