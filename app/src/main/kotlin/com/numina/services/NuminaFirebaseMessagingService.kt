package com.numina.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.numina.R
import com.numina.data.db.NotificationEntity
import com.numina.data.repository.NotificationsRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@AndroidEntryPoint
class NuminaFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var notificationsRepository: NotificationsRepository

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    companion object {
        private const val TAG = "FCMService"
        private const val CHANNEL_ID = "numina_notifications"
        private const val CHANNEL_NAME = "Numina Notifications"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "Message received from: ${remoteMessage.from}")

        // Handle notification payload
        remoteMessage.notification?.let { notification ->
            val title = notification.title ?: "Numina"
            val body = notification.body ?: ""
            val data = remoteMessage.data
            val type = data["type"] ?: "message"

            // Show system notification
            showSystemNotification(title, body, type, data)

            // Save notification to local database
            saveNotificationLocally(title, body, type, data)
        }

        // Handle data payload (when app is in foreground)
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: ${remoteMessage.data}")
            handleDataPayload(remoteMessage.data)
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "New FCM token: $token")

        // Register the new token with the backend
        serviceScope.launch {
            try {
                notificationsRepository.registerDevice(token).collect { result ->
                    when (result) {
                        is com.numina.data.repository.Result.Success -> {
                            Log.d(TAG, "Token registered successfully")
                        }
                        is com.numina.data.repository.Result.Error -> {
                            Log.e(TAG, "Failed to register token: ${result.message}")
                        }
                        else -> {}
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error registering token", e)
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for messages, matches, and updates"
                enableLights(true)
                enableVibration(true)
            }

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showSystemNotification(
        title: String,
        body: String,
        type: String,
        data: Map<String, String>
    ) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create intent for notification tap
        val intent = packageManager.getLaunchIntentForPackage(packageName)?.apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("notification_type", type)
            data.forEach { (key, value) ->
                putExtra(key, value)
            }
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(getNotificationIcon(type))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    private fun getNotificationIcon(type: String): Int {
        // Return appropriate icon based on notification type
        // For now, using a default icon (you should add proper icons to res/drawable)
        return android.R.drawable.ic_dialog_info
    }

    private fun saveNotificationLocally(
        title: String,
        body: String,
        type: String,
        data: Map<String, String>
    ) {
        serviceScope.launch {
            try {
                val notificationEntity = NotificationEntity(
                    id = UUID.randomUUID().toString(),
                    type = type,
                    title = title,
                    body = body,
                    data = if (data.isNotEmpty()) {
                        com.google.gson.Gson().toJson(data)
                    } else null,
                    isRead = false,
                    createdAt = java.time.Instant.now().toString()
                )

                notificationsRepository.getCachedNotifications().collect { /* trigger flow */ }
                // Note: In production, you'd want to insert directly via DAO
                // This is a simplified version
            } catch (e: Exception) {
                Log.e(TAG, "Error saving notification locally", e)
            }
        }
    }

    private fun handleDataPayload(data: Map<String, String>) {
        // Handle custom data payloads
        val type = data["type"] ?: return
        val title = data["title"] ?: "Numina"
        val body = data["body"] ?: ""

        showSystemNotification(title, body, type, data)
        saveNotificationLocally(title, body, type, data)
    }

    override fun onDestroy() {
        super.onDestroy()
        // Cancel all coroutines when service is destroyed
        serviceScope.coroutineContext.cancelChildren()
    }
}

private fun kotlinx.coroutines.CoroutineContext.cancelChildren() {
    kotlinx.coroutines.cancel()
}
