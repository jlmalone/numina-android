package com.numina.data.models

import com.google.gson.annotations.SerializedName

data class Message(
    @SerializedName("id")
    val id: String,

    @SerializedName("conversation_id")
    val conversationId: String,

    @SerializedName("sender_id")
    val senderId: String,

    @SerializedName("receiver_id")
    val receiverId: String,

    @SerializedName("content")
    val content: String,

    @SerializedName("timestamp")
    val timestamp: Long,

    @SerializedName("is_read")
    val isRead: Boolean = false,

    @SerializedName("attachment_url")
    val attachmentUrl: String? = null,

    @SerializedName("attachment_type")
    val attachmentType: String? = null
)

data class Conversation(
    @SerializedName("id")
    val id: String,

    @SerializedName("participant_id")
    val participantId: String,

    @SerializedName("participant_name")
    val participantName: String,

    @SerializedName("participant_avatar")
    val participantAvatar: String? = null,

    @SerializedName("last_message")
    val lastMessage: String? = null,

    @SerializedName("last_message_timestamp")
    val lastMessageTimestamp: Long? = null,

    @SerializedName("unread_count")
    val unreadCount: Int = 0,

    @SerializedName("is_typing")
    val isTyping: Boolean = false
)

data class ConversationsResponse(
    @SerializedName("conversations")
    val conversations: List<Conversation>
)

data class MessagesResponse(
    @SerializedName("messages")
    val messages: List<Message>
)

data class SendMessageRequest(
    @SerializedName("conversation_id")
    val conversationId: String,

    @SerializedName("receiver_id")
    val receiverId: String,

    @SerializedName("content")
    val content: String,

    @SerializedName("attachment_url")
    val attachmentUrl: String? = null,

    @SerializedName("attachment_type")
    val attachmentType: String? = null
)

data class SendMessageResponse(
    @SerializedName("message")
    val message: Message
)

// WebSocket message types
sealed class WebSocketMessage {
    data class NewMessage(val message: Message) : WebSocketMessage()
    data class MessageRead(val messageId: String) : WebSocketMessage()
    data class TypingIndicator(val conversationId: String, val userId: String, val isTyping: Boolean) : WebSocketMessage()
}
