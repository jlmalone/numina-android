package com.numina.data.models

import com.google.gson.annotations.SerializedName

data class Booking(
    @SerializedName("id") val id: String,
    @SerializedName("user_id") val userId: String,
    @SerializedName("class_id") val classId: String,
    @SerializedName("class") val fitnessClass: FitnessClass,
    @SerializedName("status") val status: BookingStatus,
    @SerializedName("booked_at") val bookedAt: String, // ISO 8601
    @SerializedName("attended") val attended: Boolean = false,
    @SerializedName("attended_at") val attendedAt: String? = null,
    @SerializedName("cancelled") val cancelled: Boolean = false,
    @SerializedName("cancelled_at") val cancelledAt: String? = null,
    @SerializedName("cancellation_reason") val cancellationReason: String? = null,
    @SerializedName("reminder_sent") val reminderSent: Boolean = false,
    @SerializedName("check_in_code") val checkInCode: String? = null,
    @SerializedName("notes") val notes: String? = null
)

enum class BookingStatus(val value: String) {
    @SerializedName("confirmed")
    CONFIRMED("confirmed"),

    @SerializedName("pending")
    PENDING("pending"),

    @SerializedName("cancelled")
    CANCELLED("cancelled"),

    @SerializedName("completed")
    COMPLETED("completed"),

    @SerializedName("no_show")
    NO_SHOW("no_show")
}

data class BookingsListResponse(
    @SerializedName("bookings") val bookings: List<Booking>,
    @SerializedName("total") val total: Int,
    @SerializedName("page") val page: Int,
    @SerializedName("per_page") val perPage: Int
)

data class CreateBookingRequest(
    @SerializedName("class_id") val classId: String,
    @SerializedName("notes") val notes: String? = null
)

data class CreateBookingResponse(
    @SerializedName("booking") val booking: Booking,
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String
)

data class UpdateBookingRequest(
    @SerializedName("notes") val notes: String? = null
)

data class CancelBookingRequest(
    @SerializedName("reason") val reason: String? = null
)

data class CancelBookingResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("refund_amount") val refundAmount: Double? = null
)

data class MarkAttendedResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("booking") val booking: Booking
)

// Calendar models
data class CalendarMonth(
    @SerializedName("year") val year: Int,
    @SerializedName("month") val month: Int, // 1-12
    @SerializedName("days") val days: List<CalendarDay>
)

data class CalendarDay(
    @SerializedName("date") val date: String, // YYYY-MM-DD
    @SerializedName("bookings") val bookings: List<Booking>,
    @SerializedName("is_today") val isToday: Boolean = false
)

data class CalendarMonthResponse(
    @SerializedName("calendar") val calendar: CalendarMonth
)

data class CalendarExportResponse(
    @SerializedName("ical_url") val icalUrl: String,
    @SerializedName("ical_content") val icalContent: String
)

// Reminder preferences models
data class ReminderPreferences(
    @SerializedName("user_id") val userId: String,
    @SerializedName("email_reminders_enabled") val emailRemindersEnabled: Boolean = true,
    @SerializedName("push_reminders_enabled") val pushRemindersEnabled: Boolean = true,
    @SerializedName("reminder_1h_enabled") val reminder1hEnabled: Boolean = true,
    @SerializedName("reminder_24h_enabled") val reminder24hEnabled: Boolean = true,
    @SerializedName("quiet_hours_enabled") val quietHoursEnabled: Boolean = false,
    @SerializedName("quiet_hours_start") val quietHoursStart: String? = null, // HH:mm format (e.g., "22:00")
    @SerializedName("quiet_hours_end") val quietHoursEnd: String? = null, // HH:mm format (e.g., "08:00")
    @SerializedName("timezone") val timezone: String = "UTC"
)

data class UpdateReminderPreferencesRequest(
    @SerializedName("email_reminders_enabled") val emailRemindersEnabled: Boolean? = null,
    @SerializedName("push_reminders_enabled") val pushRemindersEnabled: Boolean? = null,
    @SerializedName("reminder_1h_enabled") val reminder1hEnabled: Boolean? = null,
    @SerializedName("reminder_24h_enabled") val reminder24hEnabled: Boolean? = null,
    @SerializedName("quiet_hours_enabled") val quietHoursEnabled: Boolean? = null,
    @SerializedName("quiet_hours_start") val quietHoursStart: String? = null,
    @SerializedName("quiet_hours_end") val quietHoursEnd: String? = null
)

data class ReminderPreferencesResponse(
    @SerializedName("preferences") val preferences: ReminderPreferences
)

// Stats and Streak models
data class AttendanceStats(
    @SerializedName("total_classes_attended") val totalClassesAttended: Int,
    @SerializedName("total_classes_booked") val totalClassesBooked: Int,
    @SerializedName("attendance_rate") val attendanceRate: Double, // 0.0 - 1.0
    @SerializedName("classes_by_type") val classesByType: Map<String, Int>,
    @SerializedName("favorite_trainer") val favoriteTrainer: FavoriteTrainer? = null,
    @SerializedName("monthly_attendance") val monthlyAttendance: List<MonthlyAttendance>,
    @SerializedName("achievements") val achievements: List<Achievement>
)

data class FavoriteTrainer(
    @SerializedName("trainer_id") val trainerId: String,
    @SerializedName("trainer_name") val trainerName: String,
    @SerializedName("classes_attended") val classesAttended: Int
)

data class MonthlyAttendance(
    @SerializedName("year_month") val yearMonth: String, // YYYY-MM format
    @SerializedName("classes_attended") val classesAttended: Int,
    @SerializedName("classes_booked") val classesBooked: Int
)

data class Achievement(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String,
    @SerializedName("icon_url") val iconUrl: String? = null,
    @SerializedName("earned_at") val earnedAt: String, // ISO 8601
    @SerializedName("badge_type") val badgeType: BadgeType
)

enum class BadgeType(val value: String) {
    @SerializedName("bronze")
    BRONZE("bronze"),

    @SerializedName("silver")
    SILVER("silver"),

    @SerializedName("gold")
    GOLD("gold"),

    @SerializedName("platinum")
    PLATINUM("platinum")
}

data class AttendanceStatsResponse(
    @SerializedName("stats") val stats: AttendanceStats
)

data class WorkoutStreak(
    @SerializedName("current_streak") val currentStreak: Int,
    @SerializedName("longest_streak") val longestStreak: Int,
    @SerializedName("streak_start_date") val streakStartDate: String? = null, // ISO 8601
    @SerializedName("last_workout_date") val lastWorkoutDate: String? = null // ISO 8601
)

data class WorkoutStreakResponse(
    @SerializedName("streak") val streak: WorkoutStreak
)
