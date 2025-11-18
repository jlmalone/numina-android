package com.numina.data.api

import com.numina.data.models.*
import retrofit2.Response
import retrofit2.http.*

interface SocialApi {
    // Activity Feed
    @GET("api/v1/social/feed")
    suspend fun getActivityFeed(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<ActivityFeedResponse>

    @POST("api/v1/social/activity/{activityId}/like")
    suspend fun likeActivity(
        @Path("activityId") activityId: String
    ): Response<Activity>

    @DELETE("api/v1/social/activity/{activityId}/like")
    suspend fun unlikeActivity(
        @Path("activityId") activityId: String
    ): Response<Activity>

    @GET("api/v1/social/activity/{activityId}/comments")
    suspend fun getComments(
        @Path("activityId") activityId: String
    ): Response<CommentsResponse>

    @POST("api/v1/social/activity/{activityId}/comment")
    suspend fun addComment(
        @Path("activityId") activityId: String,
        @Body request: CommentRequest
    ): Response<Comment>

    // Following
    @POST("api/v1/social/follow/{userId}")
    suspend fun followUser(
        @Path("userId") userId: String
    ): Response<FollowResponse>

    @DELETE("api/v1/social/unfollow/{userId}")
    suspend fun unfollowUser(
        @Path("userId") userId: String
    ): Response<FollowResponse>

    @GET("api/v1/social/followers")
    suspend fun getFollowers(): Response<FollowersResponse>

    @GET("api/v1/social/following")
    suspend fun getFollowing(): Response<FollowingResponse>

    @GET("api/v1/social/users/{userId}/followers")
    suspend fun getUserFollowers(
        @Path("userId") userId: String
    ): Response<FollowersResponse>

    @GET("api/v1/social/users/{userId}/following")
    suspend fun getUserFollowing(
        @Path("userId") userId: String
    ): Response<FollowingResponse>

    // User Discovery
    @GET("api/v1/social/discover-users")
    suspend fun discoverUsers(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20,
        @Query("search") searchQuery: String? = null,
        @Query("fitness_level") fitnessLevel: Int? = null,
        @Query("interests") interests: String? = null, // comma-separated
        @Query("latitude") latitude: Double? = null,
        @Query("longitude") longitude: Double? = null,
        @Query("radius") radius: Int? = null
    ): Response<DiscoverUsersResponse>

    @GET("api/v1/social/suggested-users")
    suspend fun getSuggestedUsers(
        @Query("limit") limit: Int = 10
    ): Response<DiscoverUsersResponse>

    // User Profiles
    @GET("api/v1/social/users/{userId}/profile")
    suspend fun getUserProfile(
        @Path("userId") userId: String
    ): Response<UserProfile>

    @GET("api/v1/social/users/{userId}/activities")
    suspend fun getUserActivities(
        @Path("userId") userId: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<ActivityFeedResponse>
}
