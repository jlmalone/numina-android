package com.numina.data.repository

import com.google.gson.Gson
import com.numina.data.api.NotificationsApi
import com.numina.data.db.NotificationDao
import com.numina.data.db.NotificationPreferencesDao
import com.numina.data.db.toEntity
import com.numina.data.db.toNotification
import com.numina.data.db.toPreferences
import com.numina.data.models.DeviceRegistrationRequest
import com.numina.data.models.ErrorResponse
import com.numina.data.models.Notification
import com.numina.data.models.NotificationPreferences
import com.numina.data.models.NotificationPreferencesRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationsRepository @Inject constructor(
    private val notificationsApi: NotificationsApi,
    private val notificationDao: NotificationDao,
    private val notificationPreferencesDao: NotificationPreferencesDao,
    private val gson: Gson
) {
    // Get cached notifications from local database
    fun getCachedNotifications(limit: Int = 50): Flow<List<Notification>> {
        return notificationDao.getNotifications(limit).map { entities ->
            entities.map { it.toNotification() }
        }
    }

    // Get unread count
    fun getUnreadCount(): Flow<Int> {
        return notificationDao.getUnreadCount()
    }

    // Fetch notifications from API
    suspend fun fetchNotifications(
        page: Int = 1,
        perPage: Int = 50
    ): Flow<Result<List<Notification>>> = flow {
        emit(Result.Loading)
        try {
            val response = notificationsApi.getNotificationHistory(page, perPage)
            if (response.isSuccessful && response.body() != null) {
                val notifications = response.body()!!.notifications
                // Cache the notifications
                notificationDao.insertNotifications(
                    notifications.map { it.toEntity() }
                )
                emit(Result.Success(notifications))
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = if (errorBody != null) {
                    try {
                        val errorResponse = gson.fromJson(errorBody, ErrorResponse::class.java)
                        errorResponse.message
                    } catch (e: Exception) {
                        "Failed to fetch notifications: ${response.message()}"
                    }
                } else {
                    "Failed to fetch notifications: ${response.message()}"
                }
                emit(Result.Error(errorMessage))
            }
        } catch (e: Exception) {
            emit(Result.Error("Network error: ${e.message}", e))
        }
    }

    // Mark notification as read
    suspend fun markAsRead(notificationId: String): Flow<Result<Unit>> = flow {
        emit(Result.Loading)
        try {
            // Update local database first
            notificationDao.markAsRead(notificationId)

            // Sync with backend
            val response = notificationsApi.markNotificationAsRead(notificationId)
            if (response.isSuccessful) {
                emit(Result.Success(Unit))
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = if (errorBody != null) {
                    try {
                        val errorResponse = gson.fromJson(errorBody, ErrorResponse::class.java)
                        errorResponse.message
                    } catch (e: Exception) {
                        "Failed to mark as read: ${response.message()}"
                    }
                } else {
                    "Failed to mark as read: ${response.message()}"
                }
                emit(Result.Error(errorMessage))
            }
        } catch (e: Exception) {
            // Local update succeeded, so we still emit success
            emit(Result.Success(Unit))
        }
    }

    // Mark all notifications as read
    suspend fun markAllAsRead(): Flow<Result<Unit>> = flow {
        emit(Result.Loading)
        try {
            // Update local database first
            notificationDao.markAllAsRead()

            // Sync with backend
            val response = notificationsApi.markAllAsRead()
            if (response.isSuccessful) {
                emit(Result.Success(Unit))
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = if (errorBody != null) {
                    try {
                        val errorResponse = gson.fromJson(errorBody, ErrorResponse::class.java)
                        errorResponse.message
                    } catch (e: Exception) {
                        "Failed to mark all as read: ${response.message()}"
                    }
                } else {
                    "Failed to mark all as read: ${response.message()}"
                }
                emit(Result.Error(errorMessage))
            }
        } catch (e: Exception) {
            emit(Result.Success(Unit))
        }
    }

    // Register device with FCM token
    suspend fun registerDevice(fcmToken: String): Flow<Result<Unit>> = flow {
        emit(Result.Loading)
        try {
            val response = notificationsApi.registerDevice(
                DeviceRegistrationRequest(fcmToken = fcmToken)
            )
            if (response.isSuccessful) {
                emit(Result.Success(Unit))
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = if (errorBody != null) {
                    try {
                        val errorResponse = gson.fromJson(errorBody, ErrorResponse::class.java)
                        errorResponse.message
                    } catch (e: Exception) {
                        "Failed to register device: ${response.message()}"
                    }
                } else {
                    "Failed to register device: ${response.message()}"
                }
                emit(Result.Error(errorMessage))
            }
        } catch (e: Exception) {
            emit(Result.Error("Network error: ${e.message}", e))
        }
    }

    // Get notification preferences
    fun getCachedPreferences(): Flow<NotificationPreferences?> {
        return notificationPreferencesDao.getPreferences().map { entity ->
            entity?.toPreferences()
        }
    }

    // Fetch notification preferences from API
    suspend fun fetchPreferences(): Flow<Result<NotificationPreferences>> = flow {
        emit(Result.Loading)
        try {
            val response = notificationsApi.getPreferences()
            if (response.isSuccessful && response.body() != null) {
                val preferences = response.body()!!.preferences
                // Cache the preferences
                notificationPreferencesDao.insertPreferences(preferences.toEntity())
                emit(Result.Success(preferences))
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = if (errorBody != null) {
                    try {
                        val errorResponse = gson.fromJson(errorBody, ErrorResponse::class.java)
                        errorResponse.message
                    } catch (e: Exception) {
                        "Failed to fetch preferences: ${response.message()}"
                    }
                } else {
                    "Failed to fetch preferences: ${response.message()}"
                }
                emit(Result.Error(errorMessage))
            }
        } catch (e: Exception) {
            emit(Result.Error("Network error: ${e.message}", e))
        }
    }

    // Update notification preferences
    suspend fun updatePreferences(preferences: NotificationPreferences): Flow<Result<Unit>> = flow {
        emit(Result.Loading)
        try {
            // Update local cache first
            notificationPreferencesDao.insertPreferences(preferences.toEntity())

            // Sync with backend
            val request = NotificationPreferencesRequest(
                messagesEnabled = preferences.messagesEnabled,
                matchesEnabled = preferences.matchesEnabled,
                groupsEnabled = preferences.groupsEnabled,
                remindersEnabled = preferences.remindersEnabled,
                quietHoursStart = preferences.quietHoursStart,
                quietHoursEnd = preferences.quietHoursEnd,
                emailFallbackEnabled = preferences.emailFallbackEnabled
            )
            val response = notificationsApi.updatePreferences(request)
            if (response.isSuccessful) {
                emit(Result.Success(Unit))
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = if (errorBody != null) {
                    try {
                        val errorResponse = gson.fromJson(errorBody, ErrorResponse::class.java)
                        errorResponse.message
                    } catch (e: Exception) {
                        "Failed to update preferences: ${response.message()}"
                    }
                } else {
                    "Failed to update preferences: ${response.message()}"
                }
                emit(Result.Error(errorMessage))
            }
        } catch (e: Exception) {
            emit(Result.Success(Unit))
        }
    }

    // Clear old notifications cache (7 days)
    suspend fun clearOldCache() {
        val sevenDaysAgo = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000)
        notificationDao.deleteOldNotifications(sevenDaysAgo)
    }
}
