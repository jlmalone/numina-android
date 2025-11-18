package com.numina.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.numina.data.models.NotificationPreferences

@Entity(tableName = "notification_preferences")
data class NotificationPreferencesEntity(
    @PrimaryKey val id: String = "preferences",
    val messagesEnabled: Boolean = true,
    val matchesEnabled: Boolean = true,
    val groupsEnabled: Boolean = true,
    val remindersEnabled: Boolean = true,
    val quietHoursStart: String? = null,
    val quietHoursEnd: String? = null,
    val emailFallbackEnabled: Boolean = false
)

fun NotificationPreferencesEntity.toPreferences(): NotificationPreferences {
    return NotificationPreferences(
        messagesEnabled = messagesEnabled,
        matchesEnabled = matchesEnabled,
        groupsEnabled = groupsEnabled,
        remindersEnabled = remindersEnabled,
        quietHoursStart = quietHoursStart,
        quietHoursEnd = quietHoursEnd,
        emailFallbackEnabled = emailFallbackEnabled
    )
}

fun NotificationPreferences.toEntity(): NotificationPreferencesEntity {
    return NotificationPreferencesEntity(
        id = "preferences",
        messagesEnabled = messagesEnabled,
        matchesEnabled = matchesEnabled,
        groupsEnabled = groupsEnabled,
        remindersEnabled = remindersEnabled,
        quietHoursStart = quietHoursStart,
        quietHoursEnd = quietHoursEnd,
        emailFallbackEnabled = emailFallbackEnabled
    )
}
