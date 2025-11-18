package com.numina.data.api

import com.numina.data.models.*
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface ReviewsApi {
    @POST("api/v1/reviews/classes/{classId}")
    suspend fun createReview(
        @Path("classId") classId: String,
        @Body request: CreateReviewRequest
    ): Response<Review>

    @GET("api/v1/reviews/classes/{classId}")
    suspend fun getReviews(
        @Path("classId") classId: String,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 20,
        @Query("sort_by") sortBy: String? = null
    ): Response<ReviewsListResponse>

    @PUT("api/v1/reviews/{reviewId}")
    suspend fun updateReview(
        @Path("reviewId") reviewId: String,
        @Body request: UpdateReviewRequest
    ): Response<Review>

    @DELETE("api/v1/reviews/{reviewId}")
    suspend fun deleteReview(
        @Path("reviewId") reviewId: String
    ): Response<Unit>

    @POST("api/v1/reviews/{reviewId}/helpful")
    suspend fun markHelpful(
        @Path("reviewId") reviewId: String
    ): Response<MarkHelpfulResponse>

    @GET("api/v1/reviews/my-reviews")
    suspend fun getMyReviews(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 20
    ): Response<MyReviewsResponse>

    @GET("api/v1/reviews/pending")
    suspend fun getPendingReviews(): Response<PendingReviewsResponse>

    @Multipart
    @POST("api/v1/reviews/upload-photo")
    suspend fun uploadPhoto(
        @Part photo: MultipartBody.Part
    ): Response<UploadPhotoResponse>
}

data class UploadPhotoResponse(
    val url: String
)
