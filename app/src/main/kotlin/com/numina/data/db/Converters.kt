package com.numina.data.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.numina.data.models.Availability
import com.numina.data.models.ClassLocation
import com.numina.data.models.GroupLocation
import com.numina.data.models.Location
import com.numina.data.models.RsvpCounts
import com.numina.data.models.Trainer

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromStringList(value: String?): List<String>? {
        if (value == null) return null
        val listType = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun toStringList(list: List<String>?): String? {
        return gson.toJson(list)
    }

    @TypeConverter
    fun fromLocation(value: String?): Location? {
        if (value == null) return null
        return gson.fromJson(value, Location::class.java)
    }

    @TypeConverter
    fun toLocation(location: Location?): String? {
        return gson.toJson(location)
    }

    @TypeConverter
    fun fromAvailabilityList(value: String?): List<Availability>? {
        if (value == null) return null
        val listType = object : TypeToken<List<Availability>>() {}.type
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun toAvailabilityList(list: List<Availability>?): String? {
        return gson.toJson(list)
    }

    @TypeConverter
    fun fromClassLocation(value: String?): ClassLocation? {
        if (value == null) return null
        return gson.fromJson(value, ClassLocation::class.java)
    }

    @TypeConverter
    fun toClassLocation(location: ClassLocation?): String? {
        return gson.toJson(location)
    }

    @TypeConverter
    fun fromTrainer(value: String?): Trainer? {
        if (value == null) return null
        return gson.fromJson(value, Trainer::class.java)
    }

    @TypeConverter
    fun toTrainer(trainer: Trainer?): String? {
        return gson.toJson(trainer)
    }

    @TypeConverter
    fun fromGroupLocation(value: String?): GroupLocation? {
        if (value == null) return null
        return gson.fromJson(value, GroupLocation::class.java)
    }

    @TypeConverter
    fun toGroupLocation(location: GroupLocation?): String? {
        return gson.toJson(location)
    }

    @TypeConverter
    fun fromRsvpCounts(value: String?): RsvpCounts? {
        if (value == null) return null
        return gson.fromJson(value, RsvpCounts::class.java)
    }

    @TypeConverter
    fun toRsvpCounts(counts: RsvpCounts?): String? {
        return gson.toJson(counts)
    }
}
