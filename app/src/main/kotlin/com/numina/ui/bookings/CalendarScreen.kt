package com.numina.ui.bookings

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.numina.ui.bookings.components.*
import com.numina.ui.components.ErrorScreen
import com.numina.ui.components.LoadingScreen
import java.time.YearMonth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    onBookingClick: (String) -> Unit,
    viewModel: CalendarViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Calendar") },
                actions = {
                    IconButton(onClick = { viewModel.exportCalendar() }) {
                        Icon(Icons.Default.Share, contentDescription = "Export calendar")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // View selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = uiState.currentView == CalendarView.MONTH,
                    onClick = { viewModel.changeView(CalendarView.MONTH) },
                    label = { Text("Month") }
                )
                FilterChip(
                    selected = uiState.currentView == CalendarView.WEEK,
                    onClick = { viewModel.changeView(CalendarView.WEEK) },
                    label = { Text("Week") }
                )
                FilterChip(
                    selected = uiState.currentView == CalendarView.DAY,
                    onClick = { viewModel.changeView(CalendarView.DAY) },
                    label = { Text("Day") }
                )
            }

            when {
                uiState.isLoading -> {
                    LoadingScreen()
                }
                uiState.error != null -> {
                    ErrorScreen(
                        message = uiState.error,
                        onRetry = { viewModel.loadCalendarMonth(YearMonth.from(uiState.selectedDate)) }
                    )
                }
                else -> {
                    when (uiState.currentView) {
                        CalendarView.MONTH -> {
                            MonthHeader(
                                yearMonth = YearMonth.from(uiState.selectedDate),
                                onPreviousClick = { viewModel.previousMonth() },
                                onNextClick = { viewModel.nextMonth() }
                            )

                            uiState.calendar?.let { calendar ->
                                CalendarGrid(
                                    days = calendar.days,
                                    selectedDate = uiState.selectedDate,
                                    onDateClick = { viewModel.selectDate(it) }
                                )
                            }
                        }
                        CalendarView.WEEK -> {
                            val weekStart = uiState.selectedDate.minusDays(uiState.selectedDate.dayOfWeek.value.toLong() % 7)
                            uiState.calendar?.let { calendar ->
                                val weekBookings = calendar.days
                                    .filter { day ->
                                        val date = java.time.LocalDate.parse(day.date)
                                        date.isAfter(weekStart.minusDays(1)) && date.isBefore(weekStart.plusDays(8))
                                    }
                                    .flatMap { it.bookings }

                                WeekView(
                                    bookings = weekBookings,
                                    currentWeekStart = weekStart,
                                    onBookingClick = onBookingClick
                                )
                            }
                        }
                        CalendarView.DAY -> {
                            uiState.calendar?.let { calendar ->
                                val dayBookings = calendar.days
                                    .find { it.date == uiState.selectedDate.toString() }
                                    ?.bookings ?: emptyList()

                                DaySchedule(
                                    bookings = dayBookings,
                                    onBookingClick = onBookingClick
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
