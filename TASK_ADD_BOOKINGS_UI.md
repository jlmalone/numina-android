# TASK: Add Bookings Calendar UI to Numina Android App

> **IMPORTANT**: Check for `.task-bookings-ui-completed` before starting.
> **When finished**, create `.task-bookings-ui-completed` file.

## 🎯 OBJECTIVE

Build complete bookings and calendar UI to track classes, reminders, and workout streaks.

## 📋 REQUIREMENTS

### Features

1. **Calendar Views**
   - Monthly calendar with booked classes highlighted
   - Day view showing class schedule
   - Week view (horizontal scroll)
   - Tap date to see classes
   - Color coding by class type

2. **Bookings Management**
   - List of upcoming bookings
   - Past bookings with attendance status
   - Booking details screen
   - Mark as attended/cancel
   - Quick add booking button

3. **Reminders**
   - Reminder preferences screen
   - Toggle reminders (1h, 24h before)
   - Email/push notification preferences
   - Quiet hours configuration

4. **Stats & Streaks**
   - Current workout streak display
   - Total classes attended
   - Classes by type breakdown
   - Monthly attendance graph
   - Achievement badges

5. **iCal Export**
   - Export calendar to device calendar
   - Sync booked classes

### Screens to Build

- `BookingsScreen.kt` - Main bookings list
- `CalendarScreen.kt` - Calendar views (month/week/day)
- `BookingDetailScreen.kt` - Individual booking details
- `ReminderPreferencesScreen.kt` - Reminder settings
- `AttendanceStatsScreen.kt` - Stats and streaks display

### Components

- `BookingCard.kt` - Booking list item
- `CalendarGrid.kt` - Monthly calendar component
- `DaySchedule.kt` - Day view component
- `WeekView.kt` - Week view component
- `StreakDisplay.kt` - Streak counter widget
- `StatsCard.kt` - Stats display cards

### ViewModels

- `BookingsViewModel.kt` - Manage bookings list
- `CalendarViewModel.kt` - Calendar state
- `ReminderPreferencesViewModel.kt` - Reminder settings
- `AttendanceStatsViewModel.kt` - Stats calculations

### API Integration

Connect to backend bookings endpoints:
- `GET /api/v1/bookings` - List bookings
- `POST /api/v1/bookings` - Create booking
- `PUT /api/v1/bookings/{id}` - Update booking
- `POST /api/v1/bookings/{id}/mark-attended` - Mark attended
- `POST /api/v1/bookings/{id}/cancel` - Cancel
- `GET /api/v1/calendar/month/{yyyy-MM}` - Calendar data
- `GET /api/v1/calendar/export` - iCal export
- `GET /api/v1/bookings/reminder-preferences` - Get preferences
- `PUT /api/v1/bookings/reminder-preferences` - Update preferences
- `GET /api/v1/bookings/stats` - Get stats
- `GET /api/v1/bookings/streak` - Get current streak

### Local Caching

- Cache bookings in Room
- Cache calendar data
- Offline viewing of upcoming classes

### Calendar Integration

- Use Android Calendar Provider API to sync
- Request calendar permissions

## ✅ ACCEPTANCE CRITERIA

- [ ] Users can view calendar (month/week/day)
- [ ] Bookings list displays upcoming and past
- [ ] Can create and cancel bookings
- [ ] Mark classes as attended
- [ ] Reminder preferences work
- [ ] Stats and streak display correctly
- [ ] iCal export functional
- [ ] Calendar syncs with device calendar
- [ ] Material Design 3 styling
- [ ] Offline caching works

## 📝 DELIVERABLES

- Bookings screens and components
- Calendar views
- ViewModels with business logic
- Room entities for caching
- Repository layer
- Calendar provider integration
- Navigation updates
- Tests

## 🚀 COMPLETION

1. Build: `./gradlew build`
2. Test on emulator
3. Create `.task-bookings-ui-completed`
4. Commit: "Add bookings calendar UI with stats and streaks"
5. Push: `git push -u origin claude/add-bookings-ui`

---

**Est. Time**: 60-75 min | **Priority**: HIGH
