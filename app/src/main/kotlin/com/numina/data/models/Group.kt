package com.numina.data.models

import com.google.gson.annotations.SerializedName

data class Group(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String,
    @SerializedName("category") val category: String, // fitness, yoga, running, etc.
    @SerializedName("photo_url") val photoUrl: String? = null,
    @SerializedName("privacy") val privacy: String, // public, private, invite-only
    @SerializedName("location") val location: GroupLocation? = null,
    @SerializedName("member_count") val memberCount: Int,
    @SerializedName("max_members") val maxMembers: Int? = null,
    @SerializedName("owner_id") val ownerId: String,
    @SerializedName("owner_name") val ownerName: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("is_member") val isMember: Boolean = false,
    @SerializedName("is_owner") val isOwner: Boolean = false
)

data class GroupLocation(
    @SerializedName("city") val city: String,
    @SerializedName("state") val state: String? = null,
    @SerializedName("country") val country: String,
    @SerializedName("latitude") val latitude: Double? = null,
    @SerializedName("longitude") val longitude: Double? = null
)

data class GroupMember(
    @SerializedName("user_id") val userId: String,
    @SerializedName("name") val name: String,
    @SerializedName("photo_url") val photoUrl: String? = null,
    @SerializedName("role") val role: String, // owner, admin, member
    @SerializedName("joined_at") val joinedAt: String
)

data class GroupActivity(
    @SerializedName("id") val id: String,
    @SerializedName("group_id") val groupId: String,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("date_time") val dateTime: String, // ISO 8601 format
    @SerializedName("location") val location: String? = null,
    @SerializedName("fitness_class_id") val fitnessClassId: String? = null,
    @SerializedName("creator_id") val creatorId: String,
    @SerializedName("creator_name") val creatorName: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("rsvp_status") val rsvpStatus: String? = null, // going, maybe, not_going
    @SerializedName("rsvp_counts") val rsvpCounts: RsvpCounts
)

data class RsvpCounts(
    @SerializedName("going") val going: Int,
    @SerializedName("maybe") val maybe: Int,
    @SerializedName("not_going") val notGoing: Int
)

data class GroupListResponse(
    @SerializedName("groups") val groups: List<Group>,
    @SerializedName("total") val total: Int,
    @SerializedName("page") val page: Int,
    @SerializedName("per_page") val perPage: Int
)

data class GroupMembersResponse(
    @SerializedName("members") val members: List<GroupMember>,
    @SerializedName("total") val total: Int
)

data class GroupActivitiesResponse(
    @SerializedName("activities") val activities: List<GroupActivity>,
    @SerializedName("total") val total: Int
)

data class CreateGroupRequest(
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String,
    @SerializedName("category") val category: String,
    @SerializedName("privacy") val privacy: String,
    @SerializedName("location") val location: GroupLocation? = null,
    @SerializedName("max_members") val maxMembers: Int? = null,
    @SerializedName("photo_url") val photoUrl: String? = null
)

data class CreateActivityRequest(
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("date_time") val dateTime: String,
    @SerializedName("location") val location: String? = null,
    @SerializedName("fitness_class_id") val fitnessClassId: String? = null
)

data class RsvpRequest(
    @SerializedName("status") val status: String // going, maybe, not_going
)
