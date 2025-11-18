package com.numina.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.numina.data.models.Booking
import com.numina.data.models.BookingStatus
import com.numina.data.models.FitnessClass

@Entity(tableName = "bookings")
data class BookingEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "user_id")
    val userId: String,

    @ColumnInfo(name = "class_id")
    val classId: String,

    @ColumnInfo(name = "fitness_class")
    val fitnessClass: FitnessClass, // Will use TypeConverter

    @ColumnInfo(name = "status")
    val status: String,

    @ColumnInfo(name = "booked_at")
    val bookedAt: String,

    @ColumnInfo(name = "attended")
    val attended: Boolean,

    @ColumnInfo(name = "attended_at")
    val attendedAt: String?,

    @ColumnInfo(name = "cancelled")
    val cancelled: Boolean,

    @ColumnInfo(name = "cancelled_at")
    val cancelledAt: String?,

    @ColumnInfo(name = "cancellation_reason")
    val cancellationReason: String?,

    @ColumnInfo(name = "reminder_sent")
    val reminderSent: Boolean,

    @ColumnInfo(name = "check_in_code")
    val checkInCode: String?,

    @ColumnInfo(name = "notes")
    val notes: String?,

    @ColumnInfo(name = "cached_at")
    val cachedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "reminder_preferences")
data class ReminderPreferencesEntity(
    @PrimaryKey
    @ColumnInfo(name = "user_id")
    val userId: String,

    @ColumnInfo(name = "email_reminders_enabled")
    val emailRemindersEnabled: Boolean,

    @ColumnInfo(name = "push_reminders_enabled")
    val pushRemindersEnabled: Boolean,

    @ColumnInfo(name = "reminder_1h_enabled")
    val reminder1hEnabled: Boolean,

    @ColumnInfo(name = "reminder_24h_enabled")
    val reminder24hEnabled: Boolean,

    @ColumnInfo(name = "quiet_hours_enabled")
    val quietHoursEnabled: Boolean,

    @ColumnInfo(name = "quiet_hours_start")
    val quietHoursStart: String?,

    @ColumnInfo(name = "quiet_hours_end")
    val quietHoursEnd: String?,

    @ColumnInfo(name = "timezone")
    val timezone: String,

    @ColumnInfo(name = "cached_at")
    val cachedAt: Long = System.currentTimeMillis()
)

// Extension functions for conversion
fun Booking.toEntity(): BookingEntity {
    return BookingEntity(
        id = id,
        userId = userId,
        classId = classId,
        fitnessClass = fitnessClass,
        status = status.value,
        bookedAt = bookedAt,
        attended = attended,
        attendedAt = attendedAt,
        cancelled = cancelled,
        cancelledAt = cancelledAt,
        cancellationReason = cancellationReason,
        reminderSent = reminderSent,
        checkInCode = checkInCode,
        notes = notes
    )
}

fun BookingEntity.toModel(): Booking {
    return Booking(
        id = id,
        userId = userId,
        classId = classId,
        fitnessClass = fitnessClass,
        status = BookingStatus.values().find { it.value == status } ?: BookingStatus.CONFIRMED,
        bookedAt = bookedAt,
        attended = attended,
        attendedAt = attendedAt,
        cancelled = cancelled,
        cancelledAt = cancelledAt,
        cancellationReason = cancellationReason,
        reminderSent = reminderSent,
        checkInCode = checkInCode,
        notes = notes
    )
}

fun com.numina.data.models.ReminderPreferences.toEntity(): ReminderPreferencesEntity {
    return ReminderPreferencesEntity(
        userId = userId,
        emailRemindersEnabled = emailRemindersEnabled,
        pushRemindersEnabled = pushRemindersEnabled,
        reminder1hEnabled = reminder1hEnabled,
        reminder24hEnabled = reminder24hEnabled,
        quietHoursEnabled = quietHoursEnabled,
        quietHoursStart = quietHoursStart,
        quietHoursEnd = quietHoursEnd,
        timezone = timezone
    )
}

fun ReminderPreferencesEntity.toModel(): com.numina.data.models.ReminderPreferences {
    return com.numina.data.models.ReminderPreferences(
        userId = userId,
        emailRemindersEnabled = emailRemindersEnabled,
        pushRemindersEnabled = pushRemindersEnabled,
        reminder1hEnabled = reminder1hEnabled,
        reminder24hEnabled = reminder24hEnabled,
        quietHoursEnabled = quietHoursEnabled,
        quietHoursStart = quietHoursStart,
        quietHoursEnd = quietHoursEnd,
        timezone = timezone
    )
}
