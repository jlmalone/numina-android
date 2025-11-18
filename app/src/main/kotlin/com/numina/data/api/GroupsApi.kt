package com.numina.data.api

import com.numina.data.models.CreateActivityRequest
import com.numina.data.models.CreateGroupRequest
import com.numina.data.models.Group
import com.numina.data.models.GroupActivitiesResponse
import com.numina.data.models.GroupActivity
import com.numina.data.models.GroupListResponse
import com.numina.data.models.GroupMembersResponse
import com.numina.data.models.RsvpRequest
import retrofit2.http.*

interface GroupsApi {
    @GET("api/v1/groups")
    suspend fun getGroups(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 20,
        @Query("category") category: String? = null,
        @Query("location") location: String? = null,
        @Query("search") search: String? = null
    ): GroupListResponse

    @GET("api/v1/groups/{id}")
    suspend fun getGroupById(
        @Path("id") groupId: String
    ): Group

    @POST("api/v1/groups")
    suspend fun createGroup(
        @Body request: CreateGroupRequest
    ): Group

    @POST("api/v1/groups/{id}/join")
    suspend fun joinGroup(
        @Path("id") groupId: String
    ): Group

    @POST("api/v1/groups/{id}/leave")
    suspend fun leaveGroup(
        @Path("id") groupId: String
    ): Group

    @GET("api/v1/groups/{id}/members")
    suspend fun getGroupMembers(
        @Path("id") groupId: String
    ): GroupMembersResponse

    @GET("api/v1/groups/{id}/activities")
    suspend fun getGroupActivities(
        @Path("id") groupId: String
    ): GroupActivitiesResponse

    @GET("api/v1/groups/{group_id}/activities/{activity_id}")
    suspend fun getActivityById(
        @Path("group_id") groupId: String,
        @Path("activity_id") activityId: String
    ): GroupActivity

    @POST("api/v1/groups/{id}/activities")
    suspend fun createActivity(
        @Path("id") groupId: String,
        @Body request: CreateActivityRequest
    ): GroupActivity

    @POST("api/v1/groups/{group_id}/activities/{activity_id}/rsvp")
    suspend fun rsvpToActivity(
        @Path("group_id") groupId: String,
        @Path("activity_id") activityId: String,
        @Body request: RsvpRequest
    ): GroupActivity

    @GET("api/v1/groups/my")
    suspend fun getMyGroups(): GroupListResponse
}
