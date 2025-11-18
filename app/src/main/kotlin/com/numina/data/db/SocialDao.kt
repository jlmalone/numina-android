package com.numina.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivityDao {
    @Query("SELECT * FROM activities ORDER BY created_at DESC")
    fun getAllActivities(): Flow<List<ActivityEntity>>

    @Query("SELECT * FROM activities WHERE id = :activityId")
    suspend fun getActivityById(activityId: String): ActivityEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivity(activity: ActivityEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivities(activities: List<ActivityEntity>)

    @Query("DELETE FROM activities")
    suspend fun clearActivities()

    @Query("UPDATE activities SET likes_count = :likesCount, is_liked = :isLiked WHERE id = :activityId")
    suspend fun updateLikeStatus(activityId: String, likesCount: Int, isLiked: Boolean)

    @Query("UPDATE activities SET comments_count = :commentsCount WHERE id = :activityId")
    suspend fun updateCommentsCount(activityId: String, commentsCount: Int)

    @Query("DELETE FROM activities WHERE cached_at < :timestamp")
    suspend fun deleteOldActivities(timestamp: Long)
}

@Dao
interface CommentDao {
    @Query("SELECT * FROM comments WHERE activity_id = :activityId ORDER BY created_at ASC")
    fun getCommentsForActivity(activityId: String): Flow<List<CommentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: CommentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComments(comments: List<CommentEntity>)

    @Query("DELETE FROM comments WHERE activity_id = :activityId")
    suspend fun deleteCommentsForActivity(activityId: String)
}

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profiles WHERE id = :userId")
    fun getUserProfile(userId: String): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profiles WHERE id = :userId")
    suspend fun getUserProfileSync(userId: String): UserProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProfile(profile: UserProfileEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProfiles(profiles: List<UserProfileEntity>)

    @Query("UPDATE user_profiles SET is_following = :isFollowing WHERE id = :userId")
    suspend fun updateFollowStatus(userId: String, isFollowing: Boolean)

    @Query("DELETE FROM user_profiles WHERE cached_at < :timestamp")
    suspend fun deleteOldProfiles(timestamp: Long)
}

@Dao
interface FollowingDao {
    @Query("SELECT * FROM following WHERE is_following = 1 ORDER BY name ASC")
    fun getFollowing(): Flow<List<FollowingEntity>>

    @Query("SELECT * FROM following WHERE is_mutual = 1 ORDER BY name ASC")
    fun getFollowers(): Flow<List<FollowingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFollowing(user: FollowingEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFollowingList(users: List<FollowingEntity>)

    @Query("DELETE FROM following WHERE id = :userId")
    suspend fun removeFollowing(userId: String)

    @Query("DELETE FROM following")
    suspend fun clearFollowing()
}
