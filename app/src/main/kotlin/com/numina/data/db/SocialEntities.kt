package com.numina.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.numina.data.models.*

@Entity(tableName = "activities")
data class ActivityEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "user_id") val userId: String,
    @ColumnInfo(name = "user_name") val userName: String,
    @ColumnInfo(name = "user_photo_url") val userPhotoUrl: String?,
    @ColumnInfo(name = "type") val type: String,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "description") val description: String?,
    @ColumnInfo(name = "created_at") val createdAt: String,
    @ColumnInfo(name = "likes_count") val likesCount: Int,
    @ColumnInfo(name = "comments_count") val commentsCount: Int,
    @ColumnInfo(name = "is_liked") val isLiked: Boolean,
    @ColumnInfo(name = "metadata") val metadata: String?, // JSON string
    @ColumnInfo(name = "cached_at") val cachedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "comments")
data class CommentEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "activity_id") val activityId: String,
    @ColumnInfo(name = "user_id") val userId: String,
    @ColumnInfo(name = "user_name") val userName: String,
    @ColumnInfo(name = "user_photo_url") val userPhotoUrl: String?,
    @ColumnInfo(name = "text") val text: String,
    @ColumnInfo(name = "created_at") val createdAt: String
)

@Entity(tableName = "user_profiles")
data class UserProfileEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "email") val email: String?,
    @ColumnInfo(name = "bio") val bio: String?,
    @ColumnInfo(name = "photo_url") val photoUrl: String?,
    @ColumnInfo(name = "fitness_level") val fitnessLevel: Int?,
    @ColumnInfo(name = "fitness_interests") val fitnessInterests: String?, // JSON string
    @ColumnInfo(name = "location") val location: String?, // JSON string
    @ColumnInfo(name = "followers_count") val followersCount: Int,
    @ColumnInfo(name = "following_count") val followingCount: Int,
    @ColumnInfo(name = "workouts_count") val workoutsCount: Int,
    @ColumnInfo(name = "is_following") val isFollowing: Boolean,
    @ColumnInfo(name = "is_mutual") val isMutual: Boolean,
    @ColumnInfo(name = "created_at") val createdAt: String?,
    @ColumnInfo(name = "cached_at") val cachedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "following")
data class FollowingEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "bio") val bio: String?,
    @ColumnInfo(name = "photo_url") val photoUrl: String?,
    @ColumnInfo(name = "fitness_level") val fitnessLevel: Int?,
    @ColumnInfo(name = "fitness_interests") val fitnessInterests: String?, // JSON string
    @ColumnInfo(name = "is_following") val isFollowing: Boolean,
    @ColumnInfo(name = "is_mutual") val isMutual: Boolean,
    @ColumnInfo(name = "cached_at") val cachedAt: Long = System.currentTimeMillis()
)

// Extension functions for conversion
fun Activity.toEntity(): ActivityEntity {
    val metadataJson = metadata?.let {
        com.google.gson.Gson().toJson(it)
    }
    return ActivityEntity(
        id = id,
        userId = userId,
        userName = userName,
        userPhotoUrl = userPhotoUrl,
        type = type.name,
        title = title,
        description = description,
        createdAt = createdAt,
        likesCount = likesCount,
        commentsCount = commentsCount,
        isLiked = isLiked,
        metadata = metadataJson
    )
}

fun ActivityEntity.toActivity(): Activity {
    val metadataObj = metadata?.let {
        try {
            com.google.gson.Gson().fromJson(it, ActivityMetadata::class.java)
        } catch (e: Exception) {
            null
        }
    }
    return Activity(
        id = id,
        userId = userId,
        userName = userName,
        userPhotoUrl = userPhotoUrl,
        type = ActivityType.valueOf(type),
        title = title,
        description = description,
        createdAt = createdAt,
        likesCount = likesCount,
        commentsCount = commentsCount,
        isLiked = isLiked,
        metadata = metadataObj
    )
}

fun Comment.toEntity(): CommentEntity {
    return CommentEntity(
        id = id,
        activityId = activityId,
        userId = userId,
        userName = userName,
        userPhotoUrl = userPhotoUrl,
        text = text,
        createdAt = createdAt
    )
}

fun CommentEntity.toComment(): Comment {
    return Comment(
        id = id,
        activityId = activityId,
        userId = userId,
        userName = userName,
        userPhotoUrl = userPhotoUrl,
        text = text,
        createdAt = createdAt
    )
}

fun UserProfile.toEntity(): UserProfileEntity {
    val gson = com.google.gson.Gson()
    return UserProfileEntity(
        id = id,
        name = name,
        email = email,
        bio = bio,
        photoUrl = photoUrl,
        fitnessLevel = fitnessLevel,
        fitnessInterests = fitnessInterests?.let { gson.toJson(it) },
        location = location?.let { gson.toJson(it) },
        followersCount = followersCount,
        followingCount = followingCount,
        workoutsCount = workoutsCount,
        isFollowing = isFollowing,
        isMutual = isMutual,
        createdAt = createdAt
    )
}

fun UserProfileEntity.toUserProfile(): UserProfile {
    val gson = com.google.gson.Gson()
    return UserProfile(
        id = id,
        name = name,
        email = email,
        bio = bio,
        photoUrl = photoUrl,
        fitnessLevel = fitnessLevel,
        fitnessInterests = fitnessInterests?.let {
            gson.fromJson(it, Array<String>::class.java).toList()
        },
        location = location?.let {
            gson.fromJson(it, Location::class.java)
        },
        followersCount = followersCount,
        followingCount = followingCount,
        workoutsCount = workoutsCount,
        isFollowing = isFollowing,
        isMutual = isMutual,
        createdAt = createdAt
    )
}

fun FollowUser.toEntity(): FollowingEntity {
    val gson = com.google.gson.Gson()
    return FollowingEntity(
        id = id,
        name = name,
        bio = bio,
        photoUrl = photoUrl,
        fitnessLevel = fitnessLevel,
        fitnessInterests = fitnessInterests?.let { gson.toJson(it) },
        isFollowing = isFollowing,
        isMutual = isMutual
    )
}

fun FollowingEntity.toFollowUser(): FollowUser {
    val gson = com.google.gson.Gson()
    return FollowUser(
        id = id,
        name = name,
        bio = bio,
        photoUrl = photoUrl,
        fitnessLevel = fitnessLevel,
        fitnessInterests = fitnessInterests?.let {
            gson.fromJson(it, Array<String>::class.java).toList()
        },
        isFollowing = isFollowing,
        isMutual = isMutual
    )
}
