package com.numina.data.repository

import com.google.gson.Gson
import com.numina.data.api.SocialApi
import com.numina.data.db.*
import com.numina.data.models.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SocialRepository @Inject constructor(
    private val socialApi: SocialApi,
    private val activityDao: ActivityDao,
    private val commentDao: CommentDao,
    private val userProfileDao: UserProfileDao,
    private val followingDao: FollowingDao,
    private val gson: Gson
) {
    // Activity Feed
    fun getCachedActivities(): Flow<List<Activity>> {
        return activityDao.getAllActivities().map { entities ->
            entities.map { it.toActivity() }
        }
    }

    suspend fun fetchActivityFeed(page: Int = 1, refresh: Boolean = false): Flow<Result<ActivityFeedResponse>> = flow {
        emit(Result.Loading)

        if (refresh) {
            activityDao.clearActivities()
        }

        try {
            val response = socialApi.getActivityFeed(page)
            if (response.isSuccessful && response.body() != null) {
                val feedResponse = response.body()!!
                activityDao.insertActivities(feedResponse.activities.map { it.toEntity() })
                emit(Result.Success(feedResponse))
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = errorBody?.let {
                    try {
                        gson.fromJson(it, ErrorResponse::class.java).message
                    } catch (e: Exception) {
                        "Failed to fetch feed: ${response.message()}"
                    }
                } ?: "Failed to fetch feed: ${response.message()}"
                emit(Result.Error(errorMessage))
            }
        } catch (e: Exception) {
            emit(Result.Error("Network error: ${e.message}", e))
        }
    }

    suspend fun likeActivity(activityId: String): Flow<Result<Activity>> = flow {
        emit(Result.Loading)
        try {
            val response = socialApi.likeActivity(activityId)
            if (response.isSuccessful && response.body() != null) {
                val activity = response.body()!!
                activityDao.updateLikeStatus(activityId, activity.likesCount, true)
                emit(Result.Success(activity))
            } else {
                val errorMessage = response.errorBody()?.string()?.let {
                    try {
                        gson.fromJson(it, ErrorResponse::class.java).message
                    } catch (e: Exception) {
                        "Failed to like activity"
                    }
                } ?: "Failed to like activity"
                emit(Result.Error(errorMessage))
            }
        } catch (e: Exception) {
            emit(Result.Error("Network error: ${e.message}", e))
        }
    }

    suspend fun unlikeActivity(activityId: String): Flow<Result<Activity>> = flow {
        emit(Result.Loading)
        try {
            val response = socialApi.unlikeActivity(activityId)
            if (response.isSuccessful && response.body() != null) {
                val activity = response.body()!!
                activityDao.updateLikeStatus(activityId, activity.likesCount, false)
                emit(Result.Success(activity))
            } else {
                val errorMessage = response.errorBody()?.string()?.let {
                    try {
                        gson.fromJson(it, ErrorResponse::class.java).message
                    } catch (e: Exception) {
                        "Failed to unlike activity"
                    }
                } ?: "Failed to unlike activity"
                emit(Result.Error(errorMessage))
            }
        } catch (e: Exception) {
            emit(Result.Error("Network error: ${e.message}", e))
        }
    }

    fun getCachedComments(activityId: String): Flow<List<Comment>> {
        return commentDao.getCommentsForActivity(activityId).map { entities ->
            entities.map { it.toComment() }
        }
    }

    suspend fun fetchComments(activityId: String): Flow<Result<List<Comment>>> = flow {
        emit(Result.Loading)
        try {
            val response = socialApi.getComments(activityId)
            if (response.isSuccessful && response.body() != null) {
                val comments = response.body()!!.comments
                commentDao.deleteCommentsForActivity(activityId)
                commentDao.insertComments(comments.map { it.toEntity() })
                emit(Result.Success(comments))
            } else {
                val errorMessage = response.errorBody()?.string()?.let {
                    try {
                        gson.fromJson(it, ErrorResponse::class.java).message
                    } catch (e: Exception) {
                        "Failed to fetch comments"
                    }
                } ?: "Failed to fetch comments"
                emit(Result.Error(errorMessage))
            }
        } catch (e: Exception) {
            emit(Result.Error("Network error: ${e.message}", e))
        }
    }

    suspend fun addComment(activityId: String, text: String): Flow<Result<Comment>> = flow {
        emit(Result.Loading)
        try {
            val response = socialApi.addComment(activityId, CommentRequest(text))
            if (response.isSuccessful && response.body() != null) {
                val comment = response.body()!!
                commentDao.insertComment(comment.toEntity())

                // Update comment count in activity
                val activity = activityDao.getActivityById(activityId)
                activity?.let {
                    activityDao.updateCommentsCount(activityId, it.commentsCount + 1)
                }

                emit(Result.Success(comment))
            } else {
                val errorMessage = response.errorBody()?.string()?.let {
                    try {
                        gson.fromJson(it, ErrorResponse::class.java).message
                    } catch (e: Exception) {
                        "Failed to add comment"
                    }
                } ?: "Failed to add comment"
                emit(Result.Error(errorMessage))
            }
        } catch (e: Exception) {
            emit(Result.Error("Network error: ${e.message}", e))
        }
    }

    // Following
    suspend fun followUser(userId: String): Flow<Result<FollowResponse>> = flow {
        emit(Result.Loading)
        try {
            val response = socialApi.followUser(userId)
            if (response.isSuccessful && response.body() != null) {
                val result = response.body()!!
                userProfileDao.updateFollowStatus(userId, true)
                emit(Result.Success(result))
            } else {
                val errorMessage = response.errorBody()?.string()?.let {
                    try {
                        gson.fromJson(it, ErrorResponse::class.java).message
                    } catch (e: Exception) {
                        "Failed to follow user"
                    }
                } ?: "Failed to follow user"
                emit(Result.Error(errorMessage))
            }
        } catch (e: Exception) {
            emit(Result.Error("Network error: ${e.message}", e))
        }
    }

    suspend fun unfollowUser(userId: String): Flow<Result<FollowResponse>> = flow {
        emit(Result.Loading)
        try {
            val response = socialApi.unfollowUser(userId)
            if (response.isSuccessful && response.body() != null) {
                val result = response.body()!!
                userProfileDao.updateFollowStatus(userId, false)
                emit(Result.Success(result))
            } else {
                val errorMessage = response.errorBody()?.string()?.let {
                    try {
                        gson.fromJson(it, ErrorResponse::class.java).message
                    } catch (e: Exception) {
                        "Failed to unfollow user"
                    }
                } ?: "Failed to unfollow user"
                emit(Result.Error(errorMessage))
            }
        } catch (e: Exception) {
            emit(Result.Error("Network error: ${e.message}", e))
        }
    }

    fun getCachedFollowing(): Flow<List<FollowUser>> {
        return followingDao.getFollowing().map { entities ->
            entities.map { it.toFollowUser() }
        }
    }

    suspend fun fetchFollowing(): Flow<Result<List<FollowUser>>> = flow {
        emit(Result.Loading)
        try {
            val response = socialApi.getFollowing()
            if (response.isSuccessful && response.body() != null) {
                val users = response.body()!!.users
                followingDao.clearFollowing()
                followingDao.insertFollowingList(users.map { it.toEntity() })
                emit(Result.Success(users))
            } else {
                val errorMessage = response.errorBody()?.string()?.let {
                    try {
                        gson.fromJson(it, ErrorResponse::class.java).message
                    } catch (e: Exception) {
                        "Failed to fetch following"
                    }
                } ?: "Failed to fetch following"
                emit(Result.Error(errorMessage))
            }
        } catch (e: Exception) {
            emit(Result.Error("Network error: ${e.message}", e))
        }
    }

    suspend fun fetchFollowers(): Flow<Result<List<FollowUser>>> = flow {
        emit(Result.Loading)
        try {
            val response = socialApi.getFollowers()
            if (response.isSuccessful && response.body() != null) {
                val users = response.body()!!.users
                emit(Result.Success(users))
            } else {
                val errorMessage = response.errorBody()?.string()?.let {
                    try {
                        gson.fromJson(it, ErrorResponse::class.java).message
                    } catch (e: Exception) {
                        "Failed to fetch followers"
                    }
                } ?: "Failed to fetch followers"
                emit(Result.Error(errorMessage))
            }
        } catch (e: Exception) {
            emit(Result.Error("Network error: ${e.message}", e))
        }
    }

    // User Discovery
    suspend fun discoverUsers(
        page: Int = 1,
        searchQuery: String? = null,
        fitnessLevel: Int? = null,
        interests: List<String>? = null,
        latitude: Double? = null,
        longitude: Double? = null,
        radius: Int? = null
    ): Flow<Result<DiscoverUsersResponse>> = flow {
        emit(Result.Loading)
        try {
            val interestsString = interests?.joinToString(",")
            val response = socialApi.discoverUsers(
                page = page,
                searchQuery = searchQuery,
                fitnessLevel = fitnessLevel,
                interests = interestsString,
                latitude = latitude,
                longitude = longitude,
                radius = radius
            )
            if (response.isSuccessful && response.body() != null) {
                val result = response.body()!!
                userProfileDao.insertUserProfiles(result.users.map { it.toEntity() })
                emit(Result.Success(result))
            } else {
                val errorMessage = response.errorBody()?.string()?.let {
                    try {
                        gson.fromJson(it, ErrorResponse::class.java).message
                    } catch (e: Exception) {
                        "Failed to discover users"
                    }
                } ?: "Failed to discover users"
                emit(Result.Error(errorMessage))
            }
        } catch (e: Exception) {
            emit(Result.Error("Network error: ${e.message}", e))
        }
    }

    suspend fun getSuggestedUsers(): Flow<Result<List<UserProfile>>> = flow {
        emit(Result.Loading)
        try {
            val response = socialApi.getSuggestedUsers()
            if (response.isSuccessful && response.body() != null) {
                val users = response.body()!!.users
                userProfileDao.insertUserProfiles(users.map { it.toEntity() })
                emit(Result.Success(users))
            } else {
                val errorMessage = response.errorBody()?.string()?.let {
                    try {
                        gson.fromJson(it, ErrorResponse::class.java).message
                    } catch (e: Exception) {
                        "Failed to fetch suggested users"
                    }
                } ?: "Failed to fetch suggested users"
                emit(Result.Error(errorMessage))
            }
        } catch (e: Exception) {
            emit(Result.Error("Network error: ${e.message}", e))
        }
    }

    // User Profile
    fun getCachedUserProfile(userId: String): Flow<UserProfile?> {
        return userProfileDao.getUserProfile(userId).map { it?.toUserProfile() }
    }

    suspend fun fetchUserProfile(userId: String): Flow<Result<UserProfile>> = flow {
        emit(Result.Loading)
        try {
            val response = socialApi.getUserProfile(userId)
            if (response.isSuccessful && response.body() != null) {
                val profile = response.body()!!
                userProfileDao.insertUserProfile(profile.toEntity())
                emit(Result.Success(profile))
            } else {
                val errorMessage = response.errorBody()?.string()?.let {
                    try {
                        gson.fromJson(it, ErrorResponse::class.java).message
                    } catch (e: Exception) {
                        "Failed to fetch user profile"
                    }
                } ?: "Failed to fetch user profile"
                emit(Result.Error(errorMessage))
            }
        } catch (e: Exception) {
            emit(Result.Error("Network error: ${e.message}", e))
        }
    }

    suspend fun fetchUserActivities(userId: String, page: Int = 1): Flow<Result<ActivityFeedResponse>> = flow {
        emit(Result.Loading)
        try {
            val response = socialApi.getUserActivities(userId, page)
            if (response.isSuccessful && response.body() != null) {
                emit(Result.Success(response.body()!!))
            } else {
                val errorMessage = response.errorBody()?.string()?.let {
                    try {
                        gson.fromJson(it, ErrorResponse::class.java).message
                    } catch (e: Exception) {
                        "Failed to fetch user activities"
                    }
                } ?: "Failed to fetch user activities"
                emit(Result.Error(errorMessage))
            }
        } catch (e: Exception) {
            emit(Result.Error("Network error: ${e.message}", e))
        }
    }
}
