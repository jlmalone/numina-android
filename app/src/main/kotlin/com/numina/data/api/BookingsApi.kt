package com.numina.data.api

import com.numina.data.models.*
import retrofit2.Response
import retrofit2.http.*

interface BookingsApi {
    @GET("api/v1/bookings")
    suspend fun getBookings(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 20,
        @Query("status") status: String? = null
    ): Response<BookingsListResponse>

    @GET("api/v1/bookings/{id}")
    suspend fun getBookingById(@Path("id") bookingId: String): Response<Booking>

    @POST("api/v1/bookings")
    suspend fun createBooking(@Body request: CreateBookingRequest): Response<CreateBookingResponse>

    @PUT("api/v1/bookings/{id}")
    suspend fun updateBooking(
        @Path("id") bookingId: String,
        @Body request: UpdateBookingRequest
    ): Response<Booking>

    @POST("api/v1/bookings/{id}/mark-attended")
    suspend fun markAttended(@Path("id") bookingId: String): Response<MarkAttendedResponse>

    @POST("api/v1/bookings/{id}/cancel")
    suspend fun cancelBooking(
        @Path("id") bookingId: String,
        @Body request: CancelBookingRequest
    ): Response<CancelBookingResponse>

    @GET("api/v1/calendar/month/{yearMonth}")
    suspend fun getCalendarMonth(@Path("yearMonth") yearMonth: String): Response<CalendarMonthResponse>

    @GET("api/v1/calendar/export")
    suspend fun exportCalendar(): Response<CalendarExportResponse>

    @GET("api/v1/bookings/reminder-preferences")
    suspend fun getReminderPreferences(): Response<ReminderPreferencesResponse>

    @PUT("api/v1/bookings/reminder-preferences")
    suspend fun updateReminderPreferences(
        @Body request: UpdateReminderPreferencesRequest
    ): Response<ReminderPreferencesResponse>

    @GET("api/v1/bookings/stats")
    suspend fun getAttendanceStats(): Response<AttendanceStatsResponse>

    @GET("api/v1/bookings/streak")
    suspend fun getWorkoutStreak(): Response<WorkoutStreakResponse>
}
