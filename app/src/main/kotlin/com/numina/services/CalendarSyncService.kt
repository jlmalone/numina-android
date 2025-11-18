package com.numina.services

import android.Manifest
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.provider.CalendarContract
import androidx.core.content.ContextCompat
import com.numina.data.models.Booking
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CalendarSyncService @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        const val NUMINA_CALENDAR_NAME = "Numina Classes"
        const val NUMINA_CALENDAR_ACCOUNT = "numina_sync"
        const val NUMINA_CALENDAR_ACCOUNT_TYPE = "com.numina"
    }

    fun hasCalendarPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.WRITE_CALENDAR
        ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_CALENDAR
                ) == PackageManager.PERMISSION_GRANTED
    }

    fun syncBookingToCalendar(booking: Booking): Result<Long> {
        if (!hasCalendarPermission()) {
            return Result.failure(SecurityException("Calendar permission not granted"))
        }

        return try {
            val calendarId = getOrCreateNuminaCalendar()
            val eventId = insertOrUpdateEvent(booking, calendarId)
            Result.success(eventId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun removeBookingFromCalendar(booking: Booking): Result<Int> {
        if (!hasCalendarPermission()) {
            return Result.failure(SecurityException("Calendar permission not granted"))
        }

        return try {
            val deletedRows = context.contentResolver.delete(
                CalendarContract.Events.CONTENT_URI,
                "${CalendarContract.Events.TITLE} = ? AND ${CalendarContract.Events.DESCRIPTION} LIKE ?",
                arrayOf(booking.fitnessClass.name, "%Booking ID: ${booking.id}%")
            )
            Result.success(deletedRows)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun syncAllBookings(bookings: List<Booking>): Result<Int> {
        if (!hasCalendarPermission()) {
            return Result.failure(SecurityException("Calendar permission not granted"))
        }

        var synced = 0
        bookings.forEach { booking ->
            syncBookingToCalendar(booking).onSuccess { synced++ }
        }
        return Result.success(synced)
    }

    private fun getOrCreateNuminaCalendar(): Long {
        val contentResolver: ContentResolver = context.contentResolver

        // Check if calendar already exists
        val projection = arrayOf(CalendarContract.Calendars._ID)
        val selection = "${CalendarContract.Calendars.ACCOUNT_NAME} = ? AND ${CalendarContract.Calendars.ACCOUNT_TYPE} = ?"
        val selectionArgs = arrayOf(NUMINA_CALENDAR_ACCOUNT, NUMINA_CALENDAR_ACCOUNT_TYPE)

        contentResolver.query(
            CalendarContract.Calendars.CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            null
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                return cursor.getLong(0)
            }
        }

        // Create new calendar
        val values = ContentValues().apply {
            put(CalendarContract.Calendars.ACCOUNT_NAME, NUMINA_CALENDAR_ACCOUNT)
            put(CalendarContract.Calendars.ACCOUNT_TYPE, NUMINA_CALENDAR_ACCOUNT_TYPE)
            put(CalendarContract.Calendars.NAME, NUMINA_CALENDAR_NAME)
            put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, NUMINA_CALENDAR_NAME)
            put(CalendarContract.Calendars.CALENDAR_COLOR, 0xFF6200EE.toInt())
            put(CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL, CalendarContract.Calendars.CAL_ACCESS_OWNER)
            put(CalendarContract.Calendars.OWNER_ACCOUNT, NUMINA_CALENDAR_ACCOUNT)
            put(CalendarContract.Calendars.VISIBLE, 1)
            put(CalendarContract.Calendars.SYNC_EVENTS, 1)
        }

        val uri = contentResolver.insert(CalendarContract.Calendars.CONTENT_URI, values)
        return uri?.lastPathSegment?.toLong() ?: throw Exception("Failed to create calendar")
    }

    private fun insertOrUpdateEvent(booking: Booking, calendarId: Long): Long {
        val contentResolver: ContentResolver = context.contentResolver

        // Parse date/time
        val formatter = DateTimeFormatter.ISO_DATE_TIME
        val startTime = try {
            LocalDateTime.parse(booking.fitnessClass.dateTime, formatter)
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()
        } catch (e: Exception) {
            System.currentTimeMillis()
        }
        val endTime = startTime + (booking.fitnessClass.duration * 60 * 1000)

        val values = ContentValues().apply {
            put(CalendarContract.Events.CALENDAR_ID, calendarId)
            put(CalendarContract.Events.TITLE, booking.fitnessClass.name)
            put(CalendarContract.Events.DESCRIPTION,
                "Type: ${booking.fitnessClass.type}\n" +
                        "Trainer: ${booking.fitnessClass.trainer.name}\n" +
                        "Location: ${booking.fitnessClass.location.name}\n" +
                        "Booking ID: ${booking.id}")
            put(CalendarContract.Events.EVENT_LOCATION, booking.fitnessClass.location.address)
            put(CalendarContract.Events.DTSTART, startTime)
            put(CalendarContract.Events.DTEND, endTime)
            put(CalendarContract.Events.EVENT_TIMEZONE, ZoneId.systemDefault().id)
            put(CalendarContract.Events.STATUS, CalendarContract.Events.STATUS_CONFIRMED)
        }

        val uri = contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)
        return uri?.lastPathSegment?.toLong() ?: throw Exception("Failed to create event")
    }
}
