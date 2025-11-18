package com.numina.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.numina.data.models.ClassLocation
import com.numina.data.models.FitnessClass
import com.numina.data.models.Trainer

@Entity(tableName = "fitness_classes")
data class FitnessClassEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val type: String,
    val dateTime: String,
    val duration: Int,
    val location: ClassLocation,
    val trainer: Trainer,
    val intensity: Int,
    val price: Double,
    val currency: String,
    val bookingUrl: String,
    val provider: String,
    val imageUrl: String?,
    val capacity: Int?,
    val spotsAvailable: Int?,
    val cachedAt: Long = System.currentTimeMillis()
)

fun FitnessClassEntity.toFitnessClass(): FitnessClass {
    return FitnessClass(
        id = id,
        name = name,
        description = description,
        type = type,
        dateTime = dateTime,
        duration = duration,
        location = location,
        trainer = trainer,
        intensity = intensity,
        price = price,
        currency = currency,
        bookingUrl = bookingUrl,
        provider = provider,
        imageUrl = imageUrl,
        capacity = capacity,
        spotsAvailable = spotsAvailable
    )
}

fun FitnessClass.toEntity(): FitnessClassEntity {
    return FitnessClassEntity(
        id = id,
        name = name,
        description = description,
        type = type,
        dateTime = dateTime,
        duration = duration,
        location = location,
        trainer = trainer,
        intensity = intensity,
        price = price,
        currency = currency,
        bookingUrl = bookingUrl,
        provider = provider,
        imageUrl = imageUrl,
        capacity = capacity,
        spotsAvailable = spotsAvailable
    )
}
