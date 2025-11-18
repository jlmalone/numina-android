package com.numina.data.api

import com.numina.data.models.UpdateProfileRequest
import com.numina.data.models.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT

interface UserApi {
    @GET("api/v1/users/me")
    suspend fun getCurrentUser(): Response<User>

    @PUT("api/v1/users/me")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): Response<User>
}
