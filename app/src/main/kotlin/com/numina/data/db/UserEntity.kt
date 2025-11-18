package com.numina.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.numina.data.models.Availability
import com.numina.data.models.Location
import com.numina.data.models.User

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val email: String,
    val name: String,
    val bio: String?,
    val photoUrl: String?,
    val fitnessLevel: Int?,
    val fitnessInterests: List<String>?,
    val location: Location?,
    val availability: List<Availability>?,
    val createdAt: String?,
    val cachedAt: Long = System.currentTimeMillis()
)

fun UserEntity.toUser(): User {
    return User(
        id = id,
        email = email,
        name = name,
        bio = bio,
        photoUrl = photoUrl,
        fitnessLevel = fitnessLevel,
        fitnessInterests = fitnessInterests,
        location = location,
        availability = availability,
        createdAt = createdAt
    )
}

fun User.toEntity(): UserEntity {
    return UserEntity(
        id = id,
        email = email,
        name = name,
        bio = bio,
        photoUrl = photoUrl,
        fitnessLevel = fitnessLevel,
        fitnessInterests = fitnessInterests,
        location = location,
        availability = availability,
        createdAt = createdAt
    )
}
