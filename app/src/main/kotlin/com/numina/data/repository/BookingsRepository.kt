package com.numina.data.repository

import com.google.gson.Gson
import com.numina.data.api.BookingsApi
import com.numina.data.db.BookingDao
import com.numina.data.db.ReminderPreferencesDao
import com.numina.data.db.toEntity
import com.numina.data.db.toModel
import com.numina.data.models.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookingsRepository @Inject constructor(
    private val bookingsApi: BookingsApi,
    private val bookingDao: BookingDao,
    private val reminderPreferencesDao: ReminderPreferencesDao,
    private val tokenManager: TokenManager,
    private val gson: Gson
) {
    // Cached bookings
    fun getCachedBookings(): Flow<List<Booking>> {
        val userId = tokenManager.getUserId() ?: return flow { emit(emptyList()) }
        return bookingDao.getAllBookings(userId).map { entities ->
            entities.map { it.toModel() }
        }
    }

    fun getCachedUpcomingBookings(): Flow<List<Booking>> {
        val userId = tokenManager.getUserId() ?: return flow { emit(emptyList()) }
        return bookingDao.getUpcomingBookings(userId).map { entities ->
            entities.map { it.toModel() }
        }
    }

    fun getCachedPastBookings(): Flow<List<Booking>> {
        val userId = tokenManager.getUserId() ?: return flow { emit(emptyList()) }
        return bookingDao.getPastBookings(userId).map { entities ->
            entities.map { it.toModel() }
        }
    }

    fun getCachedBookingById(bookingId: String): Flow<Booking?> {
        return bookingDao.getBookingById(bookingId).map { entity ->
            entity?.toModel()
        }
    }

    // Fetch bookings from API
    suspend fun fetchBookings(
        page: Int = 1,
        perPage: Int = 20,
        status: String? = null
    ): Flow<Result<BookingsListResponse>> = flow {
        emit(Result.Loading)
        try {
            val response = bookingsApi.getBookings(page, perPage, status)
            if (response.isSuccessful && response.body() != null) {
                val bookingsResponse = response.body()!!
                // Cache the bookings
                bookingDao.insertBookings(
                    bookingsResponse.bookings.map { it.toEntity() }
                )
                emit(Result.Success(bookingsResponse))
            } else {
                val errorMessage = parseErrorMessage(response.errorBody()?.string(), "Failed to fetch bookings")
                emit(Result.Error(errorMessage))
            }
        } catch (e: Exception) {
            emit(Result.Error("Network error: ${e.message}", e))
        }
    }

    suspend fun fetchBookingById(bookingId: String): Flow<Result<Booking>> = flow {
        emit(Result.Loading)
        try {
            val response = bookingsApi.getBookingById(bookingId)
            if (response.isSuccessful && response.body() != null) {
                val booking = response.body()!!
                bookingDao.insertBooking(booking.toEntity())
                emit(Result.Success(booking))
            } else {
                val errorMessage = parseErrorMessage(response.errorBody()?.string(), "Failed to fetch booking")
                emit(Result.Error(errorMessage))
            }
        } catch (e: Exception) {
            emit(Result.Error("Network error: ${e.message}", e))
        }
    }

    suspend fun createBooking(request: CreateBookingRequest): Flow<Result<CreateBookingResponse>> = flow {
        emit(Result.Loading)
        try {
            val response = bookingsApi.createBooking(request)
            if (response.isSuccessful && response.body() != null) {
                val createResponse = response.body()!!
                bookingDao.insertBooking(createResponse.booking.toEntity())
                emit(Result.Success(createResponse))
            } else {
                val errorMessage = parseErrorMessage(response.errorBody()?.string(), "Failed to create booking")
                emit(Result.Error(errorMessage))
            }
        } catch (e: Exception) {
            emit(Result.Error("Network error: ${e.message}", e))
        }
    }

    suspend fun updateBooking(bookingId: String, request: UpdateBookingRequest): Flow<Result<Booking>> = flow {
        emit(Result.Loading)
        try {
            val response = bookingsApi.updateBooking(bookingId, request)
            if (response.isSuccessful && response.body() != null) {
                val booking = response.body()!!
                bookingDao.insertBooking(booking.toEntity())
                emit(Result.Success(booking))
            } else {
                val errorMessage = parseErrorMessage(response.errorBody()?.string(), "Failed to update booking")
                emit(Result.Error(errorMessage))
            }
        } catch (e: Exception) {
            emit(Result.Error("Network error: ${e.message}", e))
        }
    }

    suspend fun markAttended(bookingId: String): Flow<Result<MarkAttendedResponse>> = flow {
        emit(Result.Loading)
        try {
            val response = bookingsApi.markAttended(bookingId)
            if (response.isSuccessful && response.body() != null) {
                val markAttendedResponse = response.body()!!
                bookingDao.insertBooking(markAttendedResponse.booking.toEntity())
                emit(Result.Success(markAttendedResponse))
            } else {
                val errorMessage = parseErrorMessage(response.errorBody()?.string(), "Failed to mark as attended")
                emit(Result.Error(errorMessage))
            }
        } catch (e: Exception) {
            emit(Result.Error("Network error: ${e.message}", e))
        }
    }

    suspend fun cancelBooking(bookingId: String, request: CancelBookingRequest): Flow<Result<CancelBookingResponse>> = flow {
        emit(Result.Loading)
        try {
            val response = bookingsApi.cancelBooking(bookingId, request)
            if (response.isSuccessful && response.body() != null) {
                val cancelResponse = response.body()!!
                // Update local cache
                bookingDao.getBookingByIdSync(bookingId)?.let { booking ->
                    bookingDao.insertBooking(booking.copy(cancelled = true, cancelledAt = System.currentTimeMillis().toString()))
                }
                emit(Result.Success(cancelResponse))
            } else {
                val errorMessage = parseErrorMessage(response.errorBody()?.string(), "Failed to cancel booking")
                emit(Result.Error(errorMessage))
            }
        } catch (e: Exception) {
            emit(Result.Error("Network error: ${e.message}", e))
        }
    }

    // Calendar methods
    suspend fun fetchCalendarMonth(yearMonth: String): Flow<Result<CalendarMonthResponse>> = flow {
        emit(Result.Loading)
        try {
            val response = bookingsApi.getCalendarMonth(yearMonth)
            if (response.isSuccessful && response.body() != null) {
                val calendarResponse = response.body()!!
                emit(Result.Success(calendarResponse))
            } else {
                val errorMessage = parseErrorMessage(response.errorBody()?.string(), "Failed to fetch calendar")
                emit(Result.Error(errorMessage))
            }
        } catch (e: Exception) {
            emit(Result.Error("Network error: ${e.message}", e))
        }
    }

    suspend fun exportCalendar(): Flow<Result<CalendarExportResponse>> = flow {
        emit(Result.Loading)
        try {
            val response = bookingsApi.exportCalendar()
            if (response.isSuccessful && response.body() != null) {
                val exportResponse = response.body()!!
                emit(Result.Success(exportResponse))
            } else {
                val errorMessage = parseErrorMessage(response.errorBody()?.string(), "Failed to export calendar")
                emit(Result.Error(errorMessage))
            }
        } catch (e: Exception) {
            emit(Result.Error("Network error: ${e.message}", e))
        }
    }

    // Reminder preferences methods
    fun getCachedReminderPreferences(): Flow<ReminderPreferences?> {
        val userId = tokenManager.getUserId() ?: return flow { emit(null) }
        return reminderPreferencesDao.getReminderPreferences(userId).map { entity ->
            entity?.toModel()
        }
    }

    suspend fun fetchReminderPreferences(): Flow<Result<ReminderPreferences>> = flow {
        emit(Result.Loading)
        try {
            val response = bookingsApi.getReminderPreferences()
            if (response.isSuccessful && response.body() != null) {
                val preferences = response.body()!!.preferences
                reminderPreferencesDao.insertReminderPreferences(preferences.toEntity())
                emit(Result.Success(preferences))
            } else {
                val errorMessage = parseErrorMessage(response.errorBody()?.string(), "Failed to fetch reminder preferences")
                emit(Result.Error(errorMessage))
            }
        } catch (e: Exception) {
            emit(Result.Error("Network error: ${e.message}", e))
        }
    }

    suspend fun updateReminderPreferences(request: UpdateReminderPreferencesRequest): Flow<Result<ReminderPreferences>> = flow {
        emit(Result.Loading)
        try {
            val response = bookingsApi.updateReminderPreferences(request)
            if (response.isSuccessful && response.body() != null) {
                val preferences = response.body()!!.preferences
                reminderPreferencesDao.insertReminderPreferences(preferences.toEntity())
                emit(Result.Success(preferences))
            } else {
                val errorMessage = parseErrorMessage(response.errorBody()?.string(), "Failed to update reminder preferences")
                emit(Result.Error(errorMessage))
            }
        } catch (e: Exception) {
            emit(Result.Error("Network error: ${e.message}", e))
        }
    }

    // Stats methods
    suspend fun fetchAttendanceStats(): Flow<Result<AttendanceStats>> = flow {
        emit(Result.Loading)
        try {
            val response = bookingsApi.getAttendanceStats()
            if (response.isSuccessful && response.body() != null) {
                val stats = response.body()!!.stats
                emit(Result.Success(stats))
            } else {
                val errorMessage = parseErrorMessage(response.errorBody()?.string(), "Failed to fetch attendance stats")
                emit(Result.Error(errorMessage))
            }
        } catch (e: Exception) {
            emit(Result.Error("Network error: ${e.message}", e))
        }
    }

    suspend fun fetchWorkoutStreak(): Flow<Result<WorkoutStreak>> = flow {
        emit(Result.Loading)
        try {
            val response = bookingsApi.getWorkoutStreak()
            if (response.isSuccessful && response.body() != null) {
                val streak = response.body()!!.streak
                emit(Result.Success(streak))
            } else {
                val errorMessage = parseErrorMessage(response.errorBody()?.string(), "Failed to fetch workout streak")
                emit(Result.Error(errorMessage))
            }
        } catch (e: Exception) {
            emit(Result.Error("Network error: ${e.message}", e))
        }
    }

    // Cache management
    suspend fun clearOldCache() {
        // Clear cache older than 7 days
        val sevenDaysAgo = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000)
        bookingDao.deleteOldBookings(sevenDaysAgo)
    }

    private fun parseErrorMessage(errorBody: String?, defaultMessage: String): String {
        return if (errorBody != null) {
            try {
                val errorResponse = gson.fromJson(errorBody, ErrorResponse::class.java)
                errorResponse.message
            } catch (e: Exception) {
                defaultMessage
            }
        } else {
            defaultMessage
        }
    }
}
