package com.numina.data.models

import com.google.gson.annotations.SerializedName

data class Review(
    @SerializedName("id") val id: String,
    @SerializedName("class_id") val classId: String,
    @SerializedName("user_id") val userId: String,
    @SerializedName("user_name") val userName: String,
    @SerializedName("user_photo_url") val userPhotoUrl: String? = null,
    @SerializedName("rating") val rating: Int, // 1-5 stars
    @SerializedName("title") val title: String? = null,
    @SerializedName("content") val content: String,
    @SerializedName("pros") val pros: List<String>? = null,
    @SerializedName("cons") val cons: List<String>? = null,
    @SerializedName("photos") val photos: List<String>? = null,
    @SerializedName("helpful_count") val helpfulCount: Int = 0,
    @SerializedName("is_helpful") val isHelpful: Boolean = false, // Has current user marked as helpful
    @SerializedName("created_at") val createdAt: String, // ISO 8601
    @SerializedName("updated_at") val updatedAt: String? = null,
    @SerializedName("can_edit") val canEdit: Boolean = false, // Can current user edit (within 30 days)
    @SerializedName("can_delete") val canDelete: Boolean = false // Can current user delete
)

data class ReviewsListResponse(
    @SerializedName("reviews") val reviews: List<Review>,
    @SerializedName("total") val total: Int,
    @SerializedName("page") val page: Int,
    @SerializedName("per_page") val perPage: Int,
    @SerializedName("average_rating") val averageRating: Double,
    @SerializedName("rating_breakdown") val ratingBreakdown: RatingBreakdown
)

data class RatingBreakdown(
    @SerializedName("five_stars") val fiveStars: Int = 0,
    @SerializedName("four_stars") val fourStars: Int = 0,
    @SerializedName("three_stars") val threeStars: Int = 0,
    @SerializedName("two_stars") val twoStars: Int = 0,
    @SerializedName("one_star") val oneStar: Int = 0
)

data class CreateReviewRequest(
    @SerializedName("rating") val rating: Int,
    @SerializedName("title") val title: String? = null,
    @SerializedName("content") val content: String,
    @SerializedName("pros") val pros: List<String>? = null,
    @SerializedName("cons") val cons: List<String>? = null,
    @SerializedName("photos") val photos: List<String>? = null
)

data class UpdateReviewRequest(
    @SerializedName("rating") val rating: Int,
    @SerializedName("title") val title: String? = null,
    @SerializedName("content") val content: String,
    @SerializedName("pros") val pros: List<String>? = null,
    @SerializedName("cons") val cons: List<String>? = null,
    @SerializedName("photos") val photos: List<String>? = null
)

data class MyReviewsResponse(
    @SerializedName("reviews") val reviews: List<ReviewWithClass>,
    @SerializedName("total") val total: Int
)

data class ReviewWithClass(
    @SerializedName("review") val review: Review,
    @SerializedName("class") val fitnessClass: FitnessClass
)

data class PendingReview(
    @SerializedName("class_id") val classId: String,
    @SerializedName("class_name") val className: String,
    @SerializedName("class_type") val classType: String,
    @SerializedName("class_image_url") val classImageUrl: String? = null,
    @SerializedName("attended_at") val attendedAt: String,
    @SerializedName("trainer_name") val trainerName: String
)

data class PendingReviewsResponse(
    @SerializedName("pending_reviews") val pendingReviews: List<PendingReview>,
    @SerializedName("total") val total: Int
)

data class MarkHelpfulResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("is_helpful") val isHelpful: Boolean,
    @SerializedName("helpful_count") val helpfulCount: Int
)

// Review sort options
enum class ReviewSortBy(val value: String) {
    RECENT("recent"),
    HELPFUL("helpful"),
    RATING_HIGH("rating_high"),
    RATING_LOW("rating_low")
}
