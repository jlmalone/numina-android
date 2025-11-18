package com.numina.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.numina.data.models.Notification

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey val id: String,
    val type: String, // "message", "match", "group", "reminder"
    val title: String,
    val body: String,
    val data: String?, // JSON serialized data
    val isRead: Boolean,
    val createdAt: String,
    val cachedAt: Long = System.currentTimeMillis()
)

fun NotificationEntity.toNotification(): Notification {
    return Notification(
        id = id,
        type = type,
        title = title,
        body = body,
        data = data?.let { parseJsonData(it) },
        isRead = isRead,
        createdAt = createdAt
    )
}

fun Notification.toEntity(): NotificationEntity {
    return NotificationEntity(
        id = id,
        type = type,
        title = title,
        body = body,
        data = data?.let { serializeData(it) },
        isRead = isRead,
        createdAt = createdAt
    )
}

private fun parseJsonData(json: String): Map<String, String>? {
    return try {
        com.google.gson.Gson().fromJson(json, Map::class.java) as? Map<String, String>
    } catch (e: Exception) {
        null
    }
}

private fun serializeData(data: Map<String, String>): String {
    return com.google.gson.Gson().toJson(data)
}
