package com.numina.ui.messages

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.numina.data.models.User
import com.numina.data.repository.MessagingRepository
import com.numina.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NewChatUiState(
    val users: List<User> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = ""
)

@HiltViewModel
class NewChatViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val messagingRepository: MessagingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NewChatUiState())
    val uiState: StateFlow<NewChatUiState> = _uiState.asStateFlow()

    fun searchUsers(query: String) {
        _uiState.update { it.copy(searchQuery = query) }

        if (query.isEmpty()) {
            _uiState.update { it.copy(users = emptyList()) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            // For now, we'll use a simple approach
            // In a real app, you'd call a search API endpoint
            _uiState.update {
                it.copy(
                    isLoading = false,
                    users = emptyList() // Replace with actual search results
                )
            }
        }
    }

    suspend fun createConversation(user: User): String {
        return messagingRepository.createConversation(
            participantId = user.id,
            participantName = user.name,
            participantAvatar = user.profilePicture
        )
    }
}
