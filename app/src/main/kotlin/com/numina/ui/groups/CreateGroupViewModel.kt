package com.numina.ui.groups

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.numina.data.models.CreateGroupRequest
import com.numina.data.models.Group
import com.numina.data.models.GroupLocation
import com.numina.data.repository.GroupRepository
import com.numina.data.repository.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CreateGroupUiState(
    val name: String = "",
    val description: String = "",
    val category: String = "fitness",
    val privacy: String = "public",
    val city: String = "",
    val state: String = "",
    val country: String = "",
    val maxMembers: String = "",
    val photoUrl: String = "",
    val isCreating: Boolean = false,
    val error: String? = null,
    val createdGroup: Group? = null,
    val currentStep: Int = 0
)

@HiltViewModel
class CreateGroupViewModel @Inject constructor(
    private val groupRepository: GroupRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateGroupUiState())
    val uiState: StateFlow<CreateGroupUiState> = _uiState.asStateFlow()

    fun updateName(name: String) {
        _uiState.value = _uiState.value.copy(name = name)
    }

    fun updateDescription(description: String) {
        _uiState.value = _uiState.value.copy(description = description)
    }

    fun updateCategory(category: String) {
        _uiState.value = _uiState.value.copy(category = category)
    }

    fun updatePrivacy(privacy: String) {
        _uiState.value = _uiState.value.copy(privacy = privacy)
    }

    fun updateCity(city: String) {
        _uiState.value = _uiState.value.copy(city = city)
    }

    fun updateState(state: String) {
        _uiState.value = _uiState.value.copy(state = state)
    }

    fun updateCountry(country: String) {
        _uiState.value = _uiState.value.copy(country = country)
    }

    fun updateMaxMembers(maxMembers: String) {
        _uiState.value = _uiState.value.copy(maxMembers = maxMembers)
    }

    fun updatePhotoUrl(photoUrl: String) {
        _uiState.value = _uiState.value.copy(photoUrl = photoUrl)
    }

    fun nextStep() {
        _uiState.value = _uiState.value.copy(currentStep = _uiState.value.currentStep + 1)
    }

    fun previousStep() {
        _uiState.value = _uiState.value.copy(currentStep = _uiState.value.currentStep - 1)
    }

    fun createGroup() {
        val state = _uiState.value

        if (state.name.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Group name is required")
            return
        }

        if (state.description.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Description is required")
            return
        }

        val location = if (state.city.isNotBlank() && state.country.isNotBlank()) {
            GroupLocation(
                city = state.city,
                state = state.state.takeIf { it.isNotBlank() },
                country = state.country
            )
        } else null

        val maxMembers = state.maxMembers.toIntOrNull()

        val request = CreateGroupRequest(
            name = state.name,
            description = state.description,
            category = state.category,
            privacy = state.privacy,
            location = location,
            maxMembers = maxMembers,
            photoUrl = state.photoUrl.takeIf { it.isNotBlank() }
        )

        viewModelScope.launch {
            groupRepository.createGroup(request).collect { result ->
                when (result) {
                    is Result.Loading -> {
                        _uiState.value = _uiState.value.copy(
                            isCreating = true,
                            error = null
                        )
                    }
                    is Result.Success -> {
                        _uiState.value = _uiState.value.copy(
                            isCreating = false,
                            error = null,
                            createdGroup = result.data
                        )
                    }
                    is Result.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isCreating = false,
                            error = result.message
                        )
                    }
                }
            }
        }
    }
}
