package com.numina.ui.bookings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.numina.data.models.ReminderPreferences
import com.numina.data.models.UpdateReminderPreferencesRequest
import com.numina.data.repository.BookingsRepository
import com.numina.data.repository.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ReminderPreferencesUiState(
    val preferences: ReminderPreferences? = null,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null,
    val saveSuccess: Boolean = false
)

@HiltViewModel
class ReminderPreferencesViewModel @Inject constructor(
    private val bookingsRepository: BookingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReminderPreferencesUiState())
    val uiState: StateFlow<ReminderPreferencesUiState> = _uiState.asStateFlow()

    init {
        loadPreferences()
        loadCachedPreferences()
    }

    private fun loadCachedPreferences() {
        viewModelScope.launch {
            bookingsRepository.getCachedReminderPreferences().collect { cached ->
                if (_uiState.value.preferences == null) {
                    _uiState.value = _uiState.value.copy(preferences = cached)
                }
            }
        }
    }

    fun loadPreferences() {
        viewModelScope.launch {
            bookingsRepository.fetchReminderPreferences().collect { result ->
                when (result) {
                    is Result.Loading -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = true,
                            error = null,
                            saveSuccess = false
                        )
                    }
                    is Result.Success -> {
                        _uiState.value = _uiState.value.copy(
                            preferences = result.data,
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

    fun updatePreferences(
        emailRemindersEnabled: Boolean? = null,
        pushRemindersEnabled: Boolean? = null,
        reminder1hEnabled: Boolean? = null,
        reminder24hEnabled: Boolean? = null,
        quietHoursEnabled: Boolean? = null,
        quietHoursStart: String? = null,
        quietHoursEnd: String? = null
    ) {
        viewModelScope.launch {
            val request = UpdateReminderPreferencesRequest(
                emailRemindersEnabled = emailRemindersEnabled,
                pushRemindersEnabled = pushRemindersEnabled,
                reminder1hEnabled = reminder1hEnabled,
                reminder24hEnabled = reminder24hEnabled,
                quietHoursEnabled = quietHoursEnabled,
                quietHoursStart = quietHoursStart,
                quietHoursEnd = quietHoursEnd
            )

            bookingsRepository.updateReminderPreferences(request).collect { result ->
                when (result) {
                    is Result.Loading -> {
                        _uiState.value = _uiState.value.copy(
                            isSaving = true,
                            error = null,
                            saveSuccess = false
                        )
                    }
                    is Result.Success -> {
                        _uiState.value = _uiState.value.copy(
                            preferences = result.data,
                            isSaving = false,
                            error = null,
                            saveSuccess = true
                        )
                    }
                    is Result.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isSaving = false,
                            error = result.message,
                            saveSuccess = false
                        )
                    }
                }
            }
        }
    }

    fun toggleEmailReminders() {
        val current = _uiState.value.preferences?.emailRemindersEnabled ?: true
        updatePreferences(emailRemindersEnabled = !current)
    }

    fun togglePushReminders() {
        val current = _uiState.value.preferences?.pushRemindersEnabled ?: true
        updatePreferences(pushRemindersEnabled = !current)
    }

    fun toggle1hReminder() {
        val current = _uiState.value.preferences?.reminder1hEnabled ?: true
        updatePreferences(reminder1hEnabled = !current)
    }

    fun toggle24hReminder() {
        val current = _uiState.value.preferences?.reminder24hEnabled ?: true
        updatePreferences(reminder24hEnabled = !current)
    }

    fun toggleQuietHours() {
        val current = _uiState.value.preferences?.quietHoursEnabled ?: false
        updatePreferences(quietHoursEnabled = !current)
    }

    fun setQuietHours(start: String, end: String) {
        updatePreferences(quietHoursStart = start, quietHoursEnd = end)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null, saveSuccess = false)
    }
}
