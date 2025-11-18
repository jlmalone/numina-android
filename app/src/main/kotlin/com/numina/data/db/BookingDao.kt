package com.numina.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface BookingDao {
    @Query("SELECT * FROM bookings WHERE user_id = :userId ORDER BY booked_at DESC")
    fun getAllBookings(userId: String): Flow<List<BookingEntity>>

    @Query("SELECT * FROM bookings WHERE user_id = :userId AND cancelled = 0 AND attended = 0 ORDER BY booked_at ASC")
    fun getUpcomingBookings(userId: String): Flow<List<BookingEntity>>

    @Query("SELECT * FROM bookings WHERE user_id = :userId AND (cancelled = 1 OR attended = 1) ORDER BY booked_at DESC")
    fun getPastBookings(userId: String): Flow<List<BookingEntity>>

    @Query("SELECT * FROM bookings WHERE id = :bookingId")
    fun getBookingById(bookingId: String): Flow<BookingEntity?>

    @Query("SELECT * FROM bookings WHERE id = :bookingId")
    suspend fun getBookingByIdSync(bookingId: String): BookingEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooking(booking: BookingEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookings(bookings: List<BookingEntity>)

    @Update
    suspend fun updateBooking(booking: BookingEntity)

    @Delete
    suspend fun deleteBooking(booking: BookingEntity)

    @Query("DELETE FROM bookings WHERE user_id = :userId")
    suspend fun deleteAllBookings(userId: String)

    @Query("DELETE FROM bookings WHERE cached_at < :timestamp")
    suspend fun deleteOldBookings(timestamp: Long)

    @Query("SELECT COUNT(*) FROM bookings WHERE user_id = :userId AND cancelled = 0")
    suspend fun getActiveBookingsCount(userId: String): Int
}

@Dao
interface ReminderPreferencesDao {
    @Query("SELECT * FROM reminder_preferences WHERE user_id = :userId")
    fun getReminderPreferences(userId: String): Flow<ReminderPreferencesEntity?>

    @Query("SELECT * FROM reminder_preferences WHERE user_id = :userId")
    suspend fun getReminderPreferencesSync(userId: String): ReminderPreferencesEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminderPreferences(preferences: ReminderPreferencesEntity)

    @Update
    suspend fun updateReminderPreferences(preferences: ReminderPreferencesEntity)

    @Delete
    suspend fun deleteReminderPreferences(preferences: ReminderPreferencesEntity)

    @Query("DELETE FROM reminder_preferences WHERE user_id = :userId")
    suspend fun deleteReminderPreferencesByUserId(userId: String)
}
