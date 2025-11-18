package com.numina.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface GroupDao {
    @Query("SELECT * FROM groups ORDER BY cachedAt DESC")
    fun getAllGroups(): Flow<List<GroupEntity>>

    @Query("SELECT * FROM groups WHERE id = :groupId")
    fun getGroupById(groupId: String): Flow<GroupEntity?>

    @Query("SELECT * FROM groups WHERE isMember = 1")
    fun getMyGroups(): Flow<List<GroupEntity>>

    @Query("SELECT * FROM groups WHERE isOwner = 1")
    fun getOwnedGroups(): Flow<List<GroupEntity>>

    @Query("SELECT * FROM groups WHERE category = :category")
    fun getGroupsByCategory(category: String): Flow<List<GroupEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroup(group: GroupEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroups(groups: List<GroupEntity>)

    @Query("DELETE FROM groups WHERE id = :groupId")
    suspend fun deleteGroup(groupId: String)

    @Query("DELETE FROM groups")
    suspend fun deleteAllGroups()

    @Query("UPDATE groups SET isMember = :isMember WHERE id = :groupId")
    suspend fun updateMembershipStatus(groupId: String, isMember: Boolean)
}

@Dao
interface GroupActivityDao {
    @Query("SELECT * FROM group_activities WHERE groupId = :groupId ORDER BY dateTime ASC")
    fun getActivitiesByGroup(groupId: String): Flow<List<GroupActivityEntity>>

    @Query("SELECT * FROM group_activities WHERE id = :activityId")
    fun getActivityById(activityId: String): Flow<GroupActivityEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivity(activity: GroupActivityEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivities(activities: List<GroupActivityEntity>)

    @Query("DELETE FROM group_activities WHERE id = :activityId")
    suspend fun deleteActivity(activityId: String)

    @Query("DELETE FROM group_activities WHERE groupId = :groupId")
    suspend fun deleteActivitiesByGroup(groupId: String)

    @Query("DELETE FROM group_activities")
    suspend fun deleteAllActivities()

    @Query("UPDATE group_activities SET rsvpStatus = :status WHERE id = :activityId")
    suspend fun updateRsvpStatus(activityId: String, status: String?)
}
