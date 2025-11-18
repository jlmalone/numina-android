package com.numina.data.api

import com.numina.data.models.*
import retrofit2.Response
import retrofit2.http.*

interface MessagingApi {
    @GET("api/v1/messages/conversations")
    suspend fun getConversations(): Response<ConversationsResponse>

    @GET("api/v1/messages/conversations/{id}")
    suspend fun getConversationMessages(
        @Path("id") conversationId: String
    ): Response<MessagesResponse>

    @POST("api/v1/messages/send")
    suspend fun sendMessage(
        @Body request: SendMessageRequest
    ): Response<SendMessageResponse>

    @POST("api/v1/messages/{messageId}/read")
    suspend fun markMessageAsRead(
        @Path("messageId") messageId: String
    ): Response<Unit>

    @DELETE("api/v1/messages/conversations/{id}")
    suspend fun deleteConversation(
        @Path("id") conversationId: String
    ): Response<Unit>
}
