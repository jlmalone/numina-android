package com.numina.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ReviewDao {
    @Query("SELECT * FROM reviews WHERE classId = :classId ORDER BY createdAt DESC")
    fun getReviewsByClassId(classId: String): Flow<List<ReviewEntity>>

    @Query("SELECT * FROM reviews WHERE id = :reviewId")
    fun getReviewById(reviewId: String): Flow<ReviewEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: ReviewEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReviews(reviews: List<ReviewEntity>)

    @Query("DELETE FROM reviews WHERE id = :reviewId")
    suspend fun deleteReview(reviewId: String)

    @Query("DELETE FROM reviews WHERE classId = :classId")
    suspend fun deleteReviewsByClassId(classId: String)

    @Query("DELETE FROM reviews WHERE cachedAt < :timestamp")
    suspend fun deleteOldReviews(timestamp: Long)

    @Query("DELETE FROM reviews")
    suspend fun deleteAll()

    // Review drafts
    @Query("SELECT * FROM review_drafts WHERE classId = :classId")
    fun getDraftByClassId(classId: String): Flow<ReviewDraftEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDraft(draft: ReviewDraftEntity)

    @Query("DELETE FROM review_drafts WHERE classId = :classId")
    suspend fun deleteDraft(classId: String)

    @Query("DELETE FROM review_drafts")
    suspend fun deleteAllDrafts()
}
