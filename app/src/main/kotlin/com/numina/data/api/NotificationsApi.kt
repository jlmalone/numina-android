package com.numina.data.api

import com.numina.data.models.DeviceRegistrationRequest
import com.numina.data.models.NotificationHistoryResponse
import com.numina.data.models.NotificationPreferencesRequest
import com.numina.data.models.NotificationPreferencesResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface NotificationsApi {
    @POST("api/v1/notifications/register-device")
    suspend fun registerDevice(@Body request: DeviceRegistrationRequest): Response<Unit>

    @GET("api/v1/notifications/history")
    suspend fun getNotificationHistory(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 50
    ): Response<NotificationHistoryResponse>

    @POST("api/v1/notifications/{id}/mark-read")
    suspend fun markNotificationAsRead(@Path("id") notificationId: String): Response<Unit>

    @POST("api/v1/notifications/mark-all-read")
    suspend fun markAllAsRead(): Response<Unit>

    @GET("api/v1/notifications/preferences")
    suspend fun getPreferences(): Response<NotificationPreferencesResponse>

    @PUT("api/v1/notifications/preferences")
    suspend fun updatePreferences(@Body request: NotificationPreferencesRequest): Response<Unit>
}
