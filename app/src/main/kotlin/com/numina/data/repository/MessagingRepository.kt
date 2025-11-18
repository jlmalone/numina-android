package com.numina.data.repository

import android.util.Log
import com.numina.data.api.MessagingApi
import com.numina.data.db.ConversationDao
import com.numina.data.db.ConversationEntity
import com.numina.data.db.MessageDao
import com.numina.data.db.MessageEntity
import com.numina.data.models.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessagingRepository @Inject constructor(
    private val messagingApi: MessagingApi,
    private val messageDao: MessageDao,
    private val conversationDao: ConversationDao,
    private val webSocketManager: WebSocketManager
) {
    private val _typingUsers = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val typingUsers: StateFlow<Map<String, Boolean>> = _typingUsers.asStateFlow()

    fun getConversations(): Flow<List<Conversation>> {
        return conversationDao.getAllConversations()
            .map { entities ->
                entities.map { entity ->
                    val conversation = entity.toConversation()
                    // Add typing indicator state
                    conversation.copy(isTyping = _typingUsers.value[entity.id] ?: false)
                }
            }
    }

    fun getMessagesForConversation(conversationId: String): Flow<List<Message>> {
        return messageDao.getMessagesForConversation(conversationId)
            .map { entities -> entities.map { it.toMessage() } }
    }

    fun getTotalUnreadCount(): Flow<Int> {
        return conversationDao.getTotalUnreadCount()
            .map { it ?: 0 }
    }

    suspend fun syncConversations(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = messagingApi.getConversations()
            if (response.isSuccessful) {
                val conversations = response.body()?.conversations ?: emptyList()
                val entities = conversations.map { ConversationEntity.fromConversation(it) }
                conversationDao.insertConversations(entities)
                Result.Success(Unit)
            } else {
                Result.Error("Failed to sync conversations: ${response.message()}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing conversations", e)
            Result.Error("Error syncing conversations: ${e.message}", e)
        }
    }

    suspend fun syncMessages(conversationId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = messagingApi.getConversationMessages(conversationId)
            if (response.isSuccessful) {
                val messages = response.body()?.messages ?: emptyList()
                val entities = messages.map { MessageEntity.fromMessage(it) }
                messageDao.insertMessages(entities)
                Result.Success(Unit)
            } else {
                Result.Error("Failed to sync messages: ${response.message()}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing messages", e)
            Result.Error("Error syncing messages: ${e.message}", e)
        }
    }

    suspend fun sendMessage(
        conversationId: String,
        receiverId: String,
        content: String,
        currentUserId: String
    ): Result<Message> = withContext(Dispatchers.IO) {
        try {
            // Create a temporary message
            val tempMessage = Message(
                id = UUID.randomUUID().toString(),
                conversationId = conversationId,
                senderId = currentUserId,
                receiverId = receiverId,
                content = content,
                timestamp = System.currentTimeMillis(),
                isRead = false
            )

            // Save locally first (optimistic update)
            messageDao.insertMessage(MessageEntity.fromMessage(tempMessage, isSent = false))

            // Send to server
            val request = SendMessageRequest(
                conversationId = conversationId,
                receiverId = receiverId,
                content = content
            )

            val response = messagingApi.sendMessage(request)
            if (response.isSuccessful) {
                val sentMessage = response.body()?.message
                if (sentMessage != null) {
                    // Replace temp message with server response
                    messageDao.insertMessage(MessageEntity.fromMessage(sentMessage, isSent = true))

                    // Update conversation
                    updateConversationLastMessage(conversationId, content, sentMessage.timestamp)

                    Result.Success(sentMessage)
                } else {
                    Result.Error("No message in response")
                }
            } else {
                Result.Error("Failed to send message: ${response.message()}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error sending message", e)
            Result.Error("Error sending message: ${e.message}", e)
        }
    }

    suspend fun markConversationAsRead(conversationId: String) = withContext(Dispatchers.IO) {
        conversationDao.clearUnreadCount(conversationId)
        messageDao.markAllMessagesAsRead(conversationId)
    }

    suspend fun deleteConversation(conversationId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = messagingApi.deleteConversation(conversationId)
            if (response.isSuccessful) {
                conversationDao.deleteConversation(conversationId)
                messageDao.deleteMessagesForConversation(conversationId)
                Result.Success(Unit)
            } else {
                Result.Error("Failed to delete conversation: ${response.message()}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting conversation", e)
            Result.Error("Error deleting conversation: ${e.message}", e)
        }
    }

    suspend fun createConversation(participantId: String, participantName: String, participantAvatar: String?): String {
        // Check if conversation already exists
        val existing = conversationDao.getConversationByParticipant(participantId)
        if (existing != null) {
            return existing.id
        }

        // Create new conversation
        val conversationId = UUID.randomUUID().toString()
        val conversation = ConversationEntity(
            id = conversationId,
            participantId = participantId,
            participantName = participantName,
            participantAvatar = participantAvatar,
            lastMessage = null,
            lastMessageTimestamp = System.currentTimeMillis(),
            unreadCount = 0
        )
        conversationDao.insertConversation(conversation)
        return conversationId
    }

    fun connectWebSocket(): Flow<WebSocketMessage> {
        return webSocketManager.connect()
    }

    fun sendTypingIndicator(conversationId: String, isTyping: Boolean) {
        webSocketManager.sendTypingIndicator(conversationId, isTyping)
    }

    fun disconnectWebSocket() {
        webSocketManager.disconnect()
    }

    suspend fun handleWebSocketMessage(message: WebSocketMessage) = withContext(Dispatchers.IO) {
        when (message) {
            is WebSocketMessage.NewMessage -> {
                // Save new message
                messageDao.insertMessage(MessageEntity.fromMessage(message.message))

                // Update conversation
                updateConversationLastMessage(
                    message.message.conversationId,
                    message.message.content,
                    message.message.timestamp,
                    incrementUnread = true
                )
            }
            is WebSocketMessage.MessageRead -> {
                messageDao.markMessageAsRead(message.messageId)
            }
            is WebSocketMessage.TypingIndicator -> {
                _typingUsers.value = _typingUsers.value.toMutableMap().apply {
                    this[message.conversationId] = message.isTyping
                }
            }
        }
    }

    private suspend fun updateConversationLastMessage(
        conversationId: String,
        message: String,
        timestamp: Long,
        incrementUnread: Boolean = false
    ) {
        val conversation = conversationDao.getConversation(conversationId)
        if (conversation != null) {
            val updatedConversation = conversation.copy(
                lastMessage = message,
                lastMessageTimestamp = timestamp,
                unreadCount = if (incrementUnread) conversation.unreadCount + 1 else conversation.unreadCount
            )
            conversationDao.updateConversation(updatedConversation)
        }
    }

    companion object {
        private const val TAG = "MessagingRepository"
    }
}
