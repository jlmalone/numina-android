package com.numina.data.models

import com.google.gson.annotations.SerializedName

// Activity Feed Models
data class Activity(
    @SerializedName("id") val id: String,
    @SerializedName("user_id") val userId: String,
    @SerializedName("user_name") val userName: String,
    @SerializedName("user_photo_url") val userPhotoUrl: String? = null,
    @SerializedName("type") val type: ActivityType,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String? = null,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("likes_count") val likesCount: Int = 0,
    @SerializedName("comments_count") val commentsCount: Int = 0,
    @SerializedName("is_liked") val isLiked: Boolean = false,
    @SerializedName("metadata") val metadata: ActivityMetadata? = null
)

enum class ActivityType {
    @SerializedName("workout") WORKOUT,
    @SerializedName("group_joined") GROUP_JOINED,
    @SerializedName("review") REVIEW,
    @SerializedName("milestone") MILESTONE
}

data class ActivityMetadata(
    @SerializedName("workout_type") val workoutType: String? = null,
    @SerializedName("duration") val duration: Int? = null,
    @SerializedName("calories") val calories: Int? = null,
    @SerializedName("group_name") val groupName: String? = null,
    @SerializedName("class_name") val className: String? = null,
    @SerializedName("rating") val rating: Float? = null,
    @SerializedName("milestone_type") val milestoneType: String? = null
)

data class ActivityFeedResponse(
    @SerializedName("activities") val activities: List<Activity>,
    @SerializedName("page") val page: Int,
    @SerializedName("total_pages") val totalPages: Int,
    @SerializedName("has_more") val hasMore: Boolean
)

data class Comment(
    @SerializedName("id") val id: String,
    @SerializedName("activity_id") val activityId: String,
    @SerializedName("user_id") val userId: String,
    @SerializedName("user_name") val userName: String,
    @SerializedName("user_photo_url") val userPhotoUrl: String? = null,
    @SerializedName("text") val text: String,
    @SerializedName("created_at") val createdAt: String
)

data class CommentRequest(
    @SerializedName("text") val text: String
)

data class CommentsResponse(
    @SerializedName("comments") val comments: List<Comment>
)

// Following Models
data class UserProfile(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String? = null,
    @SerializedName("bio") val bio: String? = null,
    @SerializedName("photo_url") val photoUrl: String? = null,
    @SerializedName("fitness_level") val fitnessLevel: Int? = null,
    @SerializedName("fitness_interests") val fitnessInterests: List<String>? = null,
    @SerializedName("location") val location: Location? = null,
    @SerializedName("followers_count") val followersCount: Int = 0,
    @SerializedName("following_count") val followingCount: Int = 0,
    @SerializedName("workouts_count") val workoutsCount: Int = 0,
    @SerializedName("is_following") val isFollowing: Boolean = false,
    @SerializedName("is_mutual") val isMutual: Boolean = false,
    @SerializedName("created_at") val createdAt: String? = null
)

data class FollowUser(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("bio") val bio: String? = null,
    @SerializedName("photo_url") val photoUrl: String? = null,
    @SerializedName("fitness_level") val fitnessLevel: Int? = null,
    @SerializedName("fitness_interests") val fitnessInterests: List<String>? = null,
    @SerializedName("is_following") val isFollowing: Boolean = false,
    @SerializedName("is_mutual") val isMutual: Boolean = false
)

data class FollowersResponse(
    @SerializedName("users") val users: List<FollowUser>
)

data class FollowingResponse(
    @SerializedName("users") val users: List<FollowUser>
)

data class FollowResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String? = null
)

// User Discovery Models
data class DiscoverUsersResponse(
    @SerializedName("users") val users: List<UserProfile>,
    @SerializedName("page") val page: Int,
    @SerializedName("total_pages") val totalPages: Int,
    @SerializedName("has_more") val hasMore: Boolean
)

data class DiscoverFilters(
    @SerializedName("fitness_level") val fitnessLevel: Int? = null,
    @SerializedName("interests") val interests: List<String>? = null,
    @SerializedName("location_latitude") val locationLatitude: Double? = null,
    @SerializedName("location_longitude") val locationLongitude: Double? = null,
    @SerializedName("radius") val radius: Int? = null,
    @SerializedName("search_query") val searchQuery: String? = null
)

// Social Stats
data class SocialStats(
    @SerializedName("followers_count") val followersCount: Int = 0,
    @SerializedName("following_count") val followingCount: Int = 0,
    @SerializedName("activities_count") val activitiesCount: Int = 0,
    @SerializedName("workouts_count") val workoutsCount: Int = 0
)
