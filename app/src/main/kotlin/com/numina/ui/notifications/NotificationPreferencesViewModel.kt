package com.numina.ui.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.numina.data.models.NotificationPreferences
import com.numina.data.repository.NotificationsRepository
import com.numina.data.repository.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PreferencesUiState(
    val preferences: NotificationPreferences = NotificationPreferences(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val saveSuccess: Boolean = false
)

@HiltViewModel
class NotificationPreferencesViewModel @Inject constructor(
    private val notificationsRepository: NotificationsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PreferencesUiState())
    val uiState: StateFlow<PreferencesUiState> = _uiState.asStateFlow()

    init {
        loadPreferences()
        loadCachedPreferences()
    }

    private fun loadCachedPreferences() {
        viewModelScope.launch {
            notificationsRepository.getCachedPreferences().collect { cached ->
                cached?.let { prefs ->
                    if (!_uiState.value.isLoading) {
                        _uiState.value = _uiState.value.copy(preferences = prefs)
                    }
                }
            }
        }
    }

    fun loadPreferences() {
        viewModelScope.launch {
            notificationsRepository.fetchPreferences().collect { result ->
                when (result) {
                    is Result.Loading -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = true,
                            error = null
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

    fun updateMessagesEnabled(enabled: Boolean) {
        val updated = _uiState.value.preferences.copy(messagesEnabled = enabled)
        _uiState.value = _uiState.value.copy(preferences = updated)
    }

    fun updateMatchesEnabled(enabled: Boolean) {
        val updated = _uiState.value.preferences.copy(matchesEnabled = enabled)
        _uiState.value = _uiState.value.copy(preferences = updated)
    }

    fun updateGroupsEnabled(enabled: Boolean) {
        val updated = _uiState.value.preferences.copy(groupsEnabled = enabled)
        _uiState.value = _uiState.value.copy(preferences = updated)
    }

    fun updateRemindersEnabled(enabled: Boolean) {
        val updated = _uiState.value.preferences.copy(remindersEnabled = enabled)
        _uiState.value = _uiState.value.copy(preferences = updated)
    }

    fun updateEmailFallbackEnabled(enabled: Boolean) {
        val updated = _uiState.value.preferences.copy(emailFallbackEnabled = enabled)
        _uiState.value = _uiState.value.copy(preferences = updated)
    }

    fun updateQuietHours(start: String?, end: String?) {
        val updated = _uiState.value.preferences.copy(
            quietHoursStart = start,
            quietHoursEnd = end
        )
        _uiState.value = _uiState.value.copy(preferences = updated)
    }

    fun savePreferences() {
        viewModelScope.launch {
            notificationsRepository.updatePreferences(_uiState.value.preferences).collect { result ->
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
                            isLoading = false,
                            error = null,
                            saveSuccess = true
                        )
                    }
                    is Result.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = result.message,
                            saveSuccess = false
                        )
                    }
                }
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun clearSaveSuccess() {
        _uiState.value = _uiState.value.copy(saveSuccess = false)
    }
}
