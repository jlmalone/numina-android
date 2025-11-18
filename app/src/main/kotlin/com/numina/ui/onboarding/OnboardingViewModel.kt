package com.numina.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.numina.data.models.Availability
import com.numina.data.models.Location
import com.numina.data.models.UpdateProfileRequest
import com.numina.data.models.User
import com.numina.data.repository.Result
import com.numina.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OnboardingState(
    val currentStep: Int = 0,
    val name: String = "",
    val bio: String = "",
    val photoUrl: String? = null,
    val fitnessInterests: List<String> = emptyList(),
    val fitnessLevel: Int = 5,
    val location: Location? = null,
    val availability: List<Availability> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isComplete: Boolean = false
)

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingState())
    val uiState: StateFlow<OnboardingState> = _uiState.asStateFlow()

    fun updateName(name: String) {
        _uiState.value = _uiState.value.copy(name = name)
    }

    fun updateBio(bio: String) {
        _uiState.value = _uiState.value.copy(bio = bio)
    }

    fun updatePhotoUrl(url: String) {
        _uiState.value = _uiState.value.copy(photoUrl = url)
    }

    fun toggleInterest(interest: String) {
        val currentInterests = _uiState.value.fitnessInterests.toMutableList()
        if (currentInterests.contains(interest)) {
            currentInterests.remove(interest)
        } else {
            currentInterests.add(interest)
        }
        _uiState.value = _uiState.value.copy(fitnessInterests = currentInterests)
    }

    fun updateFitnessLevel(level: Int) {
        _uiState.value = _uiState.value.copy(fitnessLevel = level)
    }

    fun updateLocation(location: Location) {
        _uiState.value = _uiState.value.copy(location = location)
    }

    fun updateAvailability(availability: List<Availability>) {
        _uiState.value = _uiState.value.copy(availability = availability)
    }

    fun nextStep() {
        _uiState.value = _uiState.value.copy(currentStep = _uiState.value.currentStep + 1)
    }

    fun previousStep() {
        if (_uiState.value.currentStep > 0) {
            _uiState.value = _uiState.value.copy(currentStep = _uiState.value.currentStep - 1)
        }
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            val state = _uiState.value
            val request = UpdateProfileRequest(
                name = state.name.ifBlank { null },
                bio = state.bio.ifBlank { null },
                photoUrl = state.photoUrl,
                fitnessLevel = state.fitnessLevel,
                fitnessInterests = state.fitnessInterests.ifEmpty { null },
                location = state.location,
                availability = state.availability.ifEmpty { null }
            )

            userRepository.updateProfile(request).collect { result ->
                when (result) {
                    is Result.Loading -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = true,
                            error = null
                        )
                    }
                    is Result.Success -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isComplete = true,
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
}
