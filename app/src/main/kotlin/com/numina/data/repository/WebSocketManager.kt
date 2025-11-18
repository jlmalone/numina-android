package com.numina.data.repository

import android.util.Log
import com.google.gson.Gson
import com.numina.BuildConfig
import com.numina.data.models.Message
import com.numina.data.models.WebSocketMessage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import okhttp3.*
import org.json.JSONObject
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WebSocketManager @Inject constructor(
    private val tokenManager: TokenManager,
    private val gson: Gson
) {
    private var webSocket: WebSocket? = null
    private val client = OkHttpClient.Builder()
        .readTimeout(0, TimeUnit.MILLISECONDS)
        .build()

    fun connect(): Flow<WebSocketMessage> = callbackFlow {
        val token = tokenManager.getToken()
        if (token == null) {
            Log.e(TAG, "No auth token available")
            close()
            awaitClose { disconnect() }
            return@callbackFlow
        }

        val wsUrl = BuildConfig.BASE_URL
            .replace("https://", "wss://")
            .replace("http://", "ws://") + "api/v1/ws/messages?token=$token"

        val request = Request.Builder()
            .url(wsUrl)
            .build()

        val listener = object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.d(TAG, "WebSocket connected")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.d(TAG, "WebSocket message received: $text")
                try {
                    val json = JSONObject(text)
                    val type = json.optString("type")

                    when (type) {
                        "new_message" -> {
                            val messageJson = json.getJSONObject("data")
                            val message = gson.fromJson(messageJson.toString(), Message::class.java)
                            trySend(WebSocketMessage.NewMessage(message))
                        }
                        "message_read" -> {
                            val messageId = json.getJSONObject("data").getString("message_id")
                            trySend(WebSocketMessage.MessageRead(messageId))
                        }
                        "typing" -> {
                            val data = json.getJSONObject("data")
                            val conversationId = data.getString("conversation_id")
                            val userId = data.getString("user_id")
                            val isTyping = data.getBoolean("is_typing")
                            trySend(WebSocketMessage.TypingIndicator(conversationId, userId, isTyping))
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error parsing WebSocket message", e)
                }
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                Log.d(TAG, "WebSocket closing: $code $reason")
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                Log.d(TAG, "WebSocket closed: $code $reason")
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.e(TAG, "WebSocket error", t)
            }
        }

        webSocket = client.newWebSocket(request, listener)

        awaitClose {
            disconnect()
        }
    }

    fun sendTypingIndicator(conversationId: String, isTyping: Boolean) {
        webSocket?.let { ws ->
            val message = JSONObject().apply {
                put("type", "typing")
                put("data", JSONObject().apply {
                    put("conversation_id", conversationId)
                    put("is_typing", isTyping)
                })
            }
            ws.send(message.toString())
        }
    }

    fun disconnect() {
        webSocket?.close(1000, "Client disconnect")
        webSocket = null
    }

    companion object {
        private const val TAG = "WebSocketManager"
    }
}
