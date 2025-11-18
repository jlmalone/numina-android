package com.numina.ui.bookings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.numina.data.models.CalendarMonth
import com.numina.data.repository.BookingsRepository
import com.numina.data.repository.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import javax.inject.Inject

enum class CalendarView {
    MONTH, WEEK, DAY
}

data class CalendarUiState(
    val calendar: CalendarMonth? = null,
    val selectedDate: LocalDate = LocalDate.now(),
    val currentView: CalendarView = CalendarView.MONTH,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val bookingsRepository: BookingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

    init {
        loadCalendarForCurrentMonth()
    }

    private fun loadCalendarForCurrentMonth() {
        val yearMonth = YearMonth.now()
        loadCalendarMonth(yearMonth)
    }

    fun loadCalendarMonth(yearMonth: YearMonth) {
        viewModelScope.launch {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM")
            val yearMonthStr = yearMonth.format(formatter)

            bookingsRepository.fetchCalendarMonth(yearMonthStr).collect { result ->
                when (result) {
                    is Result.Loading -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = true,
                            error = null
                        )
                    }
                    is Result.Success -> {
                        _uiState.value = _uiState.value.copy(
                            calendar = result.data.calendar,
                            isLoading = false,
                            error = null
                        )
                    }
                    is Result.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = result.message
                        )
                    }
                }
            }
        }
    }

    fun selectDate(date: LocalDate) {
        _uiState.value = _uiState.value.copy(selectedDate = date)
    }

    fun changeView(view: CalendarView) {
        _uiState.value = _uiState.value.copy(currentView = view)
    }

    fun nextMonth() {
        val current = _uiState.value.selectedDate
        val next = current.plusMonths(1)
        _uiState.value = _uiState.value.copy(selectedDate = next)
        loadCalendarMonth(YearMonth.from(next))
    }

    fun previousMonth() {
        val current = _uiState.value.selectedDate
        val prev = current.minusMonths(1)
        _uiState.value = _uiState.value.copy(selectedDate = prev)
        loadCalendarMonth(YearMonth.from(prev))
    }

    fun exportCalendar() {
        viewModelScope.launch {
            bookingsRepository.exportCalendar().collect { result ->
                when (result) {
                    is Result.Loading -> {
                        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                    }
                    is Result.Success -> {
                        _uiState.value = _uiState.value.copy(isLoading = false)
                        // The export URL/content is available in result.data
                        // UI should handle opening/sharing the calendar
                    }
                    is Result.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = result.message
                        )
                    }
                }
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
