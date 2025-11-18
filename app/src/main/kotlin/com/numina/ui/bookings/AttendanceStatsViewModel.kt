package com.numina.ui.bookings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.numina.data.models.AttendanceStats
import com.numina.data.models.WorkoutStreak
import com.numina.data.repository.BookingsRepository
import com.numina.data.repository.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AttendanceStatsUiState(
    val stats: AttendanceStats? = null,
    val streak: WorkoutStreak? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class AttendanceStatsViewModel @Inject constructor(
    private val bookingsRepository: BookingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AttendanceStatsUiState())
    val uiState: StateFlow<AttendanceStatsUiState> = _uiState.asStateFlow()

    init {
        loadStats()
        loadStreak()
    }

    fun loadStats() {
        viewModelScope.launch {
            bookingsRepository.fetchAttendanceStats().collect { result ->
                when (result) {
                    is Result.Loading -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = true,
                            error = null
                        )
                    }
                    is Result.Success -> {
                        _uiState.value = _uiState.value.copy(
                            stats = result.data,
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

    fun loadStreak() {
        viewModelScope.launch {
            bookingsRepository.fetchWorkoutStreak().collect { result ->
                when (result) {
                    is Result.Loading -> {
                        // Loading handled by stats
                    }
                    is Result.Success -> {
                        _uiState.value = _uiState.value.copy(
                            streak = result.data
                        )
                    }
                    is Result.Error -> {
                        // Error handled by stats
                    }
                }
            }
        }
    }

    fun refresh() {
        loadStats()
        loadStreak()
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
