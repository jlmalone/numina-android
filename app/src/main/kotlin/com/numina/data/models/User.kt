package com.numina.data.models

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("id") val id: String,
    @SerializedName("email") val email: String,
    @SerializedName("name") val name: String,
    @SerializedName("bio") val bio: String? = null,
    @SerializedName("photo_url") val photoUrl: String? = null,
    @SerializedName("fitness_level") val fitnessLevel: Int? = null,
    @SerializedName("fitness_interests") val fitnessInterests: List<String>? = null,
    @SerializedName("location") val location: Location? = null,
    @SerializedName("availability") val availability: List<Availability>? = null,
    @SerializedName("created_at") val createdAt: String? = null
)

data class Location(
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double,
    @SerializedName("address") val address: String? = null
)

data class Availability(
    @SerializedName("day") val day: String, // Monday, Tuesday, etc.
    @SerializedName("time_slots") val timeSlots: List<String> // e.g., "morning", "afternoon", "evening"
)

data class UpdateProfileRequest(
    @SerializedName("name") val name: String? = null,
    @SerializedName("bio") val bio: String? = null,
    @SerializedName("photo_url") val photoUrl: String? = null,
    @SerializedName("fitness_level") val fitnessLevel: Int? = null,
    @SerializedName("fitness_interests") val fitnessInterests: List<String>? = null,
    @SerializedName("location") val location: Location? = null,
    @SerializedName("availability") val availability: List<Availability>? = null
)
