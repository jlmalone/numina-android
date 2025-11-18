package com.numina.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.numina.data.models.Review

@Entity(tableName = "reviews")
data class ReviewEntity(
    @PrimaryKey val id: String,
    val classId: String,
    val userId: String,
    val userName: String,
    val userPhotoUrl: String?,
    val rating: Int,
    val title: String?,
    val content: String,
    val pros: List<String>?,
    val cons: List<String>?,
    val photos: List<String>?,
    val helpfulCount: Int,
    val isHelpful: Boolean,
    val createdAt: String,
    val updatedAt: String?,
    val canEdit: Boolean,
    val canDelete: Boolean,
    val cachedAt: Long = System.currentTimeMillis()
)

fun ReviewEntity.toReview(): Review {
    return Review(
        id = id,
        classId = classId,
        userId = userId,
        userName = userName,
        userPhotoUrl = userPhotoUrl,
        rating = rating,
        title = title,
        content = content,
        pros = pros,
        cons = cons,
        photos = photos,
        helpfulCount = helpfulCount,
        isHelpful = isHelpful,
        createdAt = createdAt,
        updatedAt = updatedAt,
        canEdit = canEdit,
        canDelete = canDelete
    )
}

fun Review.toEntity(): ReviewEntity {
    return ReviewEntity(
        id = id,
        classId = classId,
        userId = userId,
        userName = userName,
        userPhotoUrl = userPhotoUrl,
        rating = rating,
        title = title,
        content = content,
        pros = pros,
        cons = cons,
        photos = photos,
        helpfulCount = helpfulCount,
        isHelpful = isHelpful,
        createdAt = createdAt,
        updatedAt = updatedAt,
        canEdit = canEdit,
        canDelete = canDelete
    )
}

@Entity(tableName = "review_drafts")
data class ReviewDraftEntity(
    @PrimaryKey val classId: String,
    val rating: Int,
    val title: String?,
    val content: String,
    val pros: List<String>?,
    val cons: List<String>?,
    val photos: List<String>?,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
