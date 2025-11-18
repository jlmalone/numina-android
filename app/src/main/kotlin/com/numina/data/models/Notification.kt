package com.numina.data.models

import com.google.gson.annotations.SerializedName

data class Notification(
    @SerializedName("id") val id: String,
    @SerializedName("type") val type: String, // "message", "match", "group", "reminder"
    @SerializedName("title") val title: String,
    @SerializedName("body") val body: String,
    @SerializedName("data") val data: Map<String, String>? = null,
    @SerializedName("is_read") val isRead: Boolean = false,
    @SerializedName("created_at") val createdAt: String
)

data class NotificationHistoryResponse(
    @SerializedName("notifications") val notifications: List<Notification>,
    @SerializedName("total") val total: Int,
    @SerializedName("page") val page: Int,
    @SerializedName("per_page") val perPage: Int
)

data class NotificationPreferences(
    @SerializedName("messages_enabled") val messagesEnabled: Boolean = true,
    @SerializedName("matches_enabled") val matchesEnabled: Boolean = true,
    @SerializedName("groups_enabled") val groupsEnabled: Boolean = true,
    @SerializedName("reminders_enabled") val remindersEnabled: Boolean = true,
    @SerializedName("quiet_hours_start") val quietHoursStart: String? = null,
    @SerializedName("quiet_hours_end") val quietHoursEnd: String? = null,
    @SerializedName("email_fallback_enabled") val emailFallbackEnabled: Boolean = false
)

data class NotificationPreferencesResponse(
    @SerializedName("preferences") val preferences: NotificationPreferences
)

data class DeviceRegistrationRequest(
    @SerializedName("fcm_token") val fcmToken: String,
    @SerializedName("device_type") val deviceType: String = "android"
)

data class NotificationPreferencesRequest(
    @SerializedName("messages_enabled") val messagesEnabled: Boolean,
    @SerializedName("matches_enabled") val matchesEnabled: Boolean,
    @SerializedName("groups_enabled") val groupsEnabled: Boolean,
    @SerializedName("reminders_enabled") val remindersEnabled: Boolean,
    @SerializedName("quiet_hours_start") val quietHoursStart: String? = null,
    @SerializedName("quiet_hours_end") val quietHoursEnd: String? = null,
    @SerializedName("email_fallback_enabled") val emailFallbackEnabled: Boolean
)
