package com.numina.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.numina.data.models.Conversation

@Entity(tableName = "conversations")
data class ConversationEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "participant_id")
    val participantId: String,

    @ColumnInfo(name = "participant_name")
    val participantName: String,

    @ColumnInfo(name = "participant_avatar")
    val participantAvatar: String? = null,

    @ColumnInfo(name = "last_message")
    val lastMessage: String? = null,

    @ColumnInfo(name = "last_message_timestamp")
    val lastMessageTimestamp: Long? = null,

    @ColumnInfo(name = "unread_count")
    val unreadCount: Int = 0
) {
    fun toConversation(): Conversation {
        return Conversation(
            id = id,
            participantId = participantId,
            participantName = participantName,
            participantAvatar = participantAvatar,
            lastMessage = lastMessage,
            lastMessageTimestamp = lastMessageTimestamp,
            unreadCount = unreadCount,
            isTyping = false
        )
    }

    companion object {
        fun fromConversation(conversation: Conversation): ConversationEntity {
            return ConversationEntity(
                id = conversation.id,
                participantId = conversation.participantId,
                participantName = conversation.participantName,
                participantAvatar = conversation.participantAvatar,
                lastMessage = conversation.lastMessage,
                lastMessageTimestamp = conversation.lastMessageTimestamp,
                unreadCount = conversation.unreadCount
            )
        }
    }
}
