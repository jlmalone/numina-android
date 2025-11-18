package com.numina.ui.bookings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.numina.data.models.Booking
import com.numina.data.models.CancelBookingRequest
import com.numina.data.models.CreateBookingRequest
import com.numina.data.repository.BookingsRepository
import com.numina.data.repository.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BookingsUiState(
    val bookings: List<Booking> = emptyList(),
    val upcomingBookings: List<Booking> = emptyList(),
    val pastBookings: List<Booking> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val showUpcoming: Boolean = true
)

@HiltViewModel
class BookingsViewModel @Inject constructor(
    private val bookingsRepository: BookingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BookingsUiState())
    val uiState: StateFlow<BookingsUiState> = _uiState.asStateFlow()

    init {
        loadBookings()
        loadCachedBookings()
    }

    private fun loadCachedBookings() {
        viewModelScope.launch {
            bookingsRepository.getCachedUpcomingBookings().collect { upcoming ->
                _uiState.value = _uiState.value.copy(upcomingBookings = upcoming)
            }
        }
        viewModelScope.launch {
            bookingsRepository.getCachedPastBookings().collect { past ->
                _uiState.value = _uiState.value.copy(pastBookings = past)
            }
        }
    }

    fun loadBookings(refresh: Boolean = false) {
        viewModelScope.launch {
            bookingsRepository.fetchBookings().collect { result ->
                when (result) {
                    is Result.Loading -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = true,
                            error = null
                        )
                    }
                    is Result.Success -> {
                        val bookings = result.data.bookings
                        val upcoming = bookings.filter { !it.cancelled && !it.attended }
                        val past = bookings.filter { it.cancelled || it.attended }
                        _uiState.value = _uiState.value.copy(
                            bookings = bookings,
                            upcomingBookings = upcoming,
                            pastBookings = past,
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

    fun createBooking(classId: String, notes: String? = null) {
        viewModelScope.launch {
            val request = CreateBookingRequest(classId, notes)
            bookingsRepository.createBooking(request).collect { result ->
                when (result) {
                    is Result.Loading -> {
                        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                    }
                    is Result.Success -> {
                        loadBookings()
                    }
                    is Result.Error -> {
                        _uiState.value = _uiState.value.copy(isLoading = false, error = result.message)
                    }
                }
            }
        }
    }

    fun cancelBooking(bookingId: String, reason: String? = null) {
        viewModelScope.launch {
            val request = CancelBookingRequest(reason)
            bookingsRepository.cancelBooking(bookingId, request).collect { result ->
                when (result) {
                    is Result.Loading -> {
                        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                    }
                    is Result.Success -> {
                        loadBookings()
                    }
                    is Result.Error -> {
                        _uiState.value = _uiState.value.copy(isLoading = false, error = result.message)
                    }
                }
            }
        }
    }

    fun markAttended(bookingId: String) {
        viewModelScope.launch {
            bookingsRepository.markAttended(bookingId).collect { result ->
                when (result) {
                    is Result.Loading -> {
                        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                    }
                    is Result.Success -> {
                        loadBookings()
                    }
                    is Result.Error -> {
                        _uiState.value = _uiState.value.copy(isLoading = false, error = result.message)
                    }
                }
            }
        }
    }

    fun toggleView() {
        _uiState.value = _uiState.value.copy(showUpcoming = !_uiState.value.showUpcoming)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
