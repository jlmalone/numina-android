package com.numina.data.models

import com.google.gson.annotations.SerializedName

data class FitnessClass(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String,
    @SerializedName("type") val type: String, // yoga, HIIT, spin, etc.
    @SerializedName("date_time") val dateTime: String, // ISO 8601 format
    @SerializedName("duration") val duration: Int, // minutes
    @SerializedName("location") val location: ClassLocation,
    @SerializedName("trainer") val trainer: Trainer,
    @SerializedName("intensity") val intensity: Int, // 1-10
    @SerializedName("price") val price: Double,
    @SerializedName("currency") val currency: String = "USD",
    @SerializedName("booking_url") val bookingUrl: String,
    @SerializedName("provider") val provider: String, // ClassPass, Mindbody, etc.
    @SerializedName("image_url") val imageUrl: String? = null,
    @SerializedName("capacity") val capacity: Int? = null,
    @SerializedName("spots_available") val spotsAvailable: Int? = null
)

data class ClassLocation(
    @SerializedName("name") val name: String, // Gym/studio name
    @SerializedName("address") val address: String,
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double
)

data class Trainer(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("bio") val bio: String? = null,
    @SerializedName("photo_url") val photoUrl: String? = null,
    @SerializedName("specialties") val specialties: List<String>? = null
)

data class ClassListResponse(
    @SerializedName("classes") val classes: List<FitnessClass>,
    @SerializedName("total") val total: Int,
    @SerializedName("page") val page: Int,
    @SerializedName("per_page") val perPage: Int
)
