package com.numina.data.repository

import com.google.gson.Gson
import com.numina.data.api.GroupsApi
import com.numina.data.db.GroupActivityDao
import com.numina.data.db.GroupDao
import com.numina.data.db.toEntity
import com.numina.data.db.toGroup
import com.numina.data.db.toGroupActivity
import com.numina.data.models.CreateActivityRequest
import com.numina.data.models.CreateGroupRequest
import com.numina.data.models.ErrorResponse
import com.numina.data.models.Group
import com.numina.data.models.GroupActivitiesResponse
import com.numina.data.models.GroupActivity
import com.numina.data.models.GroupListResponse
import com.numina.data.models.GroupMembersResponse
import com.numina.data.models.RsvpRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GroupRepository @Inject constructor(
    private val groupsApi: GroupsApi,
    private val groupDao: GroupDao,
    private val groupActivityDao: GroupActivityDao,
    private val gson: Gson
) {
    fun getCachedGroups(): Flow<List<Group>> {
        return groupDao.getAllGroups().map { entities ->
            entities.map { it.toGroup() }
        }
    }

    fun getMyGroups(): Flow<List<Group>> {
        return groupDao.getMyGroups().map { entities ->
            entities.map { it.toGroup() }
        }
    }

    fun getGroupById(groupId: String): Flow<Group?> {
        return groupDao.getGroupById(groupId).map { it?.toGroup() }
    }

    fun getGroupActivities(groupId: String): Flow<List<GroupActivity>> {
        return groupActivityDao.getActivitiesByGroup(groupId).map { entities ->
            entities.map { it.toGroupActivity() }
        }
    }

    suspend fun fetchGroups(
        page: Int = 1,
        perPage: Int = 20,
        category: String? = null,
        location: String? = null,
        search: String? = null
    ): Flow<Result<GroupListResponse>> = flow {
        emit(Result.Loading)
        try {
            val response = groupsApi.getGroups(
                page = page,
                perPage = perPage,
                category = category,
                location = location,
                search = search
            )
            // Cache the groups
            groupDao.insertGroups(response.groups.map { it.toEntity() })
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error("Network error: ${e.message}", e))
        }
    }

    suspend fun fetchGroupById(groupId: String): Flow<Result<Group>> = flow {
        emit(Result.Loading)
        try {
            val group = groupsApi.getGroupById(groupId)
            groupDao.insertGroup(group.toEntity())
            emit(Result.Success(group))
        } catch (e: Exception) {
            emit(Result.Error("Network error: ${e.message}", e))
        }
    }

    suspend fun createGroup(request: CreateGroupRequest): Flow<Result<Group>> = flow {
        emit(Result.Loading)
        try {
            val group = groupsApi.createGroup(request)
            groupDao.insertGroup(group.toEntity())
            emit(Result.Success(group))
        } catch (e: Exception) {
            emit(Result.Error("Failed to create group: ${e.message}", e))
        }
    }

    suspend fun joinGroup(groupId: String): Flow<Result<Group>> = flow {
        emit(Result.Loading)
        try {
            val group = groupsApi.joinGroup(groupId)
            groupDao.insertGroup(group.toEntity())
            emit(Result.Success(group))
        } catch (e: Exception) {
            emit(Result.Error("Failed to join group: ${e.message}", e))
        }
    }

    suspend fun leaveGroup(groupId: String): Flow<Result<Group>> = flow {
        emit(Result.Loading)
        try {
            val group = groupsApi.leaveGroup(groupId)
            groupDao.insertGroup(group.toEntity())
            emit(Result.Success(group))
        } catch (e: Exception) {
            emit(Result.Error("Failed to leave group: ${e.message}", e))
        }
    }

    suspend fun fetchGroupMembers(groupId: String): Flow<Result<GroupMembersResponse>> = flow {
        emit(Result.Loading)
        try {
            val response = groupsApi.getGroupMembers(groupId)
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error("Failed to fetch members: ${e.message}", e))
        }
    }

    suspend fun fetchGroupActivities(groupId: String): Flow<Result<GroupActivitiesResponse>> = flow {
        emit(Result.Loading)
        try {
            val response = groupsApi.getGroupActivities(groupId)
            // Cache activities
            groupActivityDao.insertActivities(response.activities.map { it.toEntity() })
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error("Failed to fetch activities: ${e.message}", e))
        }
    }

    suspend fun createActivity(
        groupId: String,
        request: CreateActivityRequest
    ): Flow<Result<GroupActivity>> = flow {
        emit(Result.Loading)
        try {
            val activity = groupsApi.createActivity(groupId, request)
            groupActivityDao.insertActivity(activity.toEntity())
            emit(Result.Success(activity))
        } catch (e: Exception) {
            emit(Result.Error("Failed to create activity: ${e.message}", e))
        }
    }

    suspend fun rsvpToActivity(
        groupId: String,
        activityId: String,
        status: String
    ): Flow<Result<GroupActivity>> = flow {
        emit(Result.Loading)
        try {
            val activity = groupsApi.rsvpToActivity(
                groupId = groupId,
                activityId = activityId,
                request = RsvpRequest(status)
            )
            groupActivityDao.insertActivity(activity.toEntity())
            emit(Result.Success(activity))
        } catch (e: Exception) {
            emit(Result.Error("Failed to RSVP: ${e.message}", e))
        }
    }

    suspend fun fetchMyGroups(): Flow<Result<GroupListResponse>> = flow {
        emit(Result.Loading)
        try {
            val response = groupsApi.getMyGroups()
            // Cache the groups
            groupDao.insertGroups(response.groups.map { it.toEntity() })
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error("Network error: ${e.message}", e))
        }
    }

    suspend fun clearOldCache() {
        // Clear cache older than 24 hours
        val oneDayAgo = System.currentTimeMillis() - (24 * 60 * 60 * 1000)
        // Note: Implement delete old methods in DAO if needed
    }
}
