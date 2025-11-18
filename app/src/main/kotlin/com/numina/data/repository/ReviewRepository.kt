package com.numina.data.repository

import com.google.gson.Gson
import com.numina.data.api.ReviewsApi
import com.numina.data.api.UploadPhotoResponse
import com.numina.data.db.ReviewDao
import com.numina.data.db.ReviewDraftEntity
import com.numina.data.db.toEntity
import com.numina.data.db.toReview
import com.numina.data.models.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReviewRepository @Inject constructor(
    private val reviewsApi: ReviewsApi,
    private val reviewDao: ReviewDao,
    private val gson: Gson
) {
    fun getCachedReviewsForClass(classId: String): Flow<List<Review>> {
        return reviewDao.getReviewsByClassId(classId).map { entities ->
            entities.map { it.toReview() }
        }
    }

    suspend fun fetchReviews(
        classId: String,
        page: Int = 1,
        perPage: Int = 20,
        sortBy: ReviewSortBy? = null
    ): Flow<Result<ReviewsListResponse>> = flow {
        emit(Result.Loading)
        try {
            val response = reviewsApi.getReviews(
                classId = classId,
                page = page,
                perPage = perPage,
                sortBy = sortBy?.value
            )
            if (response.isSuccessful && response.body() != null) {
                val reviewsResponse = response.body()!!
                // Cache the reviews
                reviewDao.insertReviews(
                    reviewsResponse.reviews.map { it.toEntity() }
                )
                emit(Result.Success(reviewsResponse))
            } else {
                val errorMessage = parseErrorMessage(response.errorBody()?.string(), "Failed to fetch reviews")
                emit(Result.Error(errorMessage))
            }
        } catch (e: Exception) {
            emit(Result.Error("Network error: ${e.message}", e))
        }
    }

    suspend fun createReview(
        classId: String,
        request: CreateReviewRequest
    ): Flow<Result<Review>> = flow {
        emit(Result.Loading)
        try {
            val response = reviewsApi.createReview(classId, request)
            if (response.isSuccessful && response.body() != null) {
                val review = response.body()!!
                reviewDao.insertReview(review.toEntity())
                // Delete draft after successful submission
                reviewDao.deleteDraft(classId)
                emit(Result.Success(review))
            } else {
                val errorMessage = parseErrorMessage(response.errorBody()?.string(), "Failed to create review")
                emit(Result.Error(errorMessage))
            }
        } catch (e: Exception) {
            emit(Result.Error("Network error: ${e.message}", e))
        }
    }

    suspend fun updateReview(
        reviewId: String,
        request: UpdateReviewRequest
    ): Flow<Result<Review>> = flow {
        emit(Result.Loading)
        try {
            val response = reviewsApi.updateReview(reviewId, request)
            if (response.isSuccessful && response.body() != null) {
                val review = response.body()!!
                reviewDao.insertReview(review.toEntity())
                emit(Result.Success(review))
            } else {
                val errorMessage = parseErrorMessage(response.errorBody()?.string(), "Failed to update review")
                emit(Result.Error(errorMessage))
            }
        } catch (e: Exception) {
            emit(Result.Error("Network error: ${e.message}", e))
        }
    }

    suspend fun deleteReview(reviewId: String): Flow<Result<Unit>> = flow {
        emit(Result.Loading)
        try {
            val response = reviewsApi.deleteReview(reviewId)
            if (response.isSuccessful) {
                reviewDao.deleteReview(reviewId)
                emit(Result.Success(Unit))
            } else {
                val errorMessage = parseErrorMessage(response.errorBody()?.string(), "Failed to delete review")
                emit(Result.Error(errorMessage))
            }
        } catch (e: Exception) {
            emit(Result.Error("Network error: ${e.message}", e))
        }
    }

    suspend fun markHelpful(reviewId: String): Flow<Result<MarkHelpfulResponse>> = flow {
        emit(Result.Loading)
        try {
            val response = reviewsApi.markHelpful(reviewId)
            if (response.isSuccessful && response.body() != null) {
                val result = response.body()!!
                emit(Result.Success(result))
            } else {
                val errorMessage = parseErrorMessage(response.errorBody()?.string(), "Failed to mark as helpful")
                emit(Result.Error(errorMessage))
            }
        } catch (e: Exception) {
            emit(Result.Error("Network error: ${e.message}", e))
        }
    }

    suspend fun getMyReviews(
        page: Int = 1,
        perPage: Int = 20
    ): Flow<Result<MyReviewsResponse>> = flow {
        emit(Result.Loading)
        try {
            val response = reviewsApi.getMyReviews(page, perPage)
            if (response.isSuccessful && response.body() != null) {
                emit(Result.Success(response.body()!!))
            } else {
                val errorMessage = parseErrorMessage(response.errorBody()?.string(), "Failed to fetch my reviews")
                emit(Result.Error(errorMessage))
            }
        } catch (e: Exception) {
            emit(Result.Error("Network error: ${e.message}", e))
        }
    }

    suspend fun getPendingReviews(): Flow<Result<PendingReviewsResponse>> = flow {
        emit(Result.Loading)
        try {
            val response = reviewsApi.getPendingReviews()
            if (response.isSuccessful && response.body() != null) {
                emit(Result.Success(response.body()!!))
            } else {
                val errorMessage = parseErrorMessage(response.errorBody()?.string(), "Failed to fetch pending reviews")
                emit(Result.Error(errorMessage))
            }
        } catch (e: Exception) {
            emit(Result.Error("Network error: ${e.message}", e))
        }
    }

    suspend fun uploadPhoto(file: File): Flow<Result<String>> = flow {
        emit(Result.Loading)
        try {
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val photoPart = MultipartBody.Part.createFormData("photo", file.name, requestFile)

            val response = reviewsApi.uploadPhoto(photoPart)
            if (response.isSuccessful && response.body() != null) {
                emit(Result.Success(response.body()!!.url))
            } else {
                val errorMessage = parseErrorMessage(response.errorBody()?.string(), "Failed to upload photo")
                emit(Result.Error(errorMessage))
            }
        } catch (e: Exception) {
            emit(Result.Error("Network error: ${e.message}", e))
        }
    }

    // Draft management
    fun getDraft(classId: String): Flow<ReviewDraftEntity?> {
        return reviewDao.getDraftByClassId(classId)
    }

    suspend fun saveDraft(draft: ReviewDraftEntity) {
        reviewDao.insertDraft(draft)
    }

    suspend fun deleteDraft(classId: String) {
        reviewDao.deleteDraft(classId)
    }

    suspend fun clearOldCache() {
        val oneDayAgo = System.currentTimeMillis() - (24 * 60 * 60 * 1000)
        reviewDao.deleteOldReviews(oneDayAgo)
    }

    private fun parseErrorMessage(errorBody: String?, defaultMessage: String): String {
        return if (errorBody != null) {
            try {
                val errorResponse = gson.fromJson(errorBody, ErrorResponse::class.java)
                errorResponse.message
            } catch (e: Exception) {
                defaultMessage
            }
        } else {
            defaultMessage
        }
    }
}
