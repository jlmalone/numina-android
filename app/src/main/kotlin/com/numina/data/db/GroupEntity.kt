package com.numina.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.numina.data.models.Group
import com.numina.data.models.GroupActivity
import com.numina.data.models.GroupLocation
import com.numina.data.models.RsvpCounts

@Entity(tableName = "groups")
data class GroupEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val category: String,
    val photoUrl: String?,
    val privacy: String,
    val location: GroupLocation?,
    val memberCount: Int,
    val maxMembers: Int?,
    val ownerId: String,
    val ownerName: String,
    val createdAt: String,
    val isMember: Boolean,
    val isOwner: Boolean,
    val cachedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "group_activities")
data class GroupActivityEntity(
    @PrimaryKey val id: String,
    val groupId: String,
    val title: String,
    val description: String,
    val dateTime: String,
    val location: String?,
    val fitnessClassId: String?,
    val creatorId: String,
    val creatorName: String,
    val createdAt: String,
    val rsvpStatus: String?,
    val rsvpCounts: RsvpCounts,
    val cachedAt: Long = System.currentTimeMillis()
)

fun GroupEntity.toGroup(): Group {
    return Group(
        id = id,
        name = name,
        description = description,
        category = category,
        photoUrl = photoUrl,
        privacy = privacy,
        location = location,
        memberCount = memberCount,
        maxMembers = maxMembers,
        ownerId = ownerId,
        ownerName = ownerName,
        createdAt = createdAt,
        isMember = isMember,
        isOwner = isOwner
    )
}

fun Group.toEntity(): GroupEntity {
    return GroupEntity(
        id = id,
        name = name,
        description = description,
        category = category,
        photoUrl = photoUrl,
        privacy = privacy,
        location = location,
        memberCount = memberCount,
        maxMembers = maxMembers,
        ownerId = ownerId,
        ownerName = ownerName,
        createdAt = createdAt,
        isMember = isMember,
        isOwner = isOwner
    )
}

fun GroupActivityEntity.toGroupActivity(): GroupActivity {
    return GroupActivity(
        id = id,
        groupId = groupId,
        title = title,
        description = description,
        dateTime = dateTime,
        location = location,
        fitnessClassId = fitnessClassId,
        creatorId = creatorId,
        creatorName = creatorName,
        createdAt = createdAt,
        rsvpStatus = rsvpStatus,
        rsvpCounts = rsvpCounts
    )
}

fun GroupActivity.toEntity(): GroupActivityEntity {
    return GroupActivityEntity(
        id = id,
        groupId = groupId,
        title = title,
        description = description,
        dateTime = dateTime,
        location = location,
        fitnessClassId = fitnessClassId,
        creatorId = creatorId,
        creatorName = creatorName,
        createdAt = createdAt,
        rsvpStatus = rsvpStatus,
        rsvpCounts = rsvpCounts
    )
}
