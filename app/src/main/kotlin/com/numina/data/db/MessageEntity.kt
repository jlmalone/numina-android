package com.numina.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.numina.data.models.Message

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "conversation_id")
    val conversationId: String,

    @ColumnInfo(name = "sender_id")
    val senderId: String,

    @ColumnInfo(name = "receiver_id")
    val receiverId: String,

    @ColumnInfo(name = "content")
    val content: String,

    @ColumnInfo(name = "timestamp")
    val timestamp: Long,

    @ColumnInfo(name = "is_read")
    val isRead: Boolean = false,

    @ColumnInfo(name = "attachment_url")
    val attachmentUrl: String? = null,

    @ColumnInfo(name = "attachment_type")
    val attachmentType: String? = null,

    @ColumnInfo(name = "is_sent")
    val isSent: Boolean = true
) {
    fun toMessage(): Message {
        return Message(
            id = id,
            conversationId = conversationId,
            senderId = senderId,
            receiverId = receiverId,
            content = content,
            timestamp = timestamp,
            isRead = isRead,
            attachmentUrl = attachmentUrl,
            attachmentType = attachmentType
        )
    }

    companion object {
        fun fromMessage(message: Message, isSent: Boolean = true): MessageEntity {
            return MessageEntity(
                id = message.id,
                conversationId = message.conversationId,
                senderId = message.senderId,
                receiverId = message.receiverId,
                content = message.content,
                timestamp = message.timestamp,
                isRead = message.isRead,
                attachmentUrl = message.attachmentUrl,
                attachmentType = message.attachmentType,
                isSent = isSent
            )
        }
    }
}
