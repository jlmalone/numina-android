package com.numina.ui.classes

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.numina.data.models.FitnessClass
import com.numina.data.repository.ClassRepository
import com.numina.data.repository.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ClassDetailsUiState(
    val fitnessClass: FitnessClass? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ClassDetailsViewModel @Inject constructor(
    private val classRepository: ClassRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val classId: String = savedStateHandle.get<String>("classId") ?: ""

    private val _uiState = MutableStateFlow(ClassDetailsUiState())
    val uiState: StateFlow<ClassDetailsUiState> = _uiState.asStateFlow()

    init {
        loadClassDetails()
    }

    fun loadClassDetails() {
        viewModelScope.launch {
            classRepository.fetchClassById(classId).collect { result ->
                when (result) {
                    is Result.Loading -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = true,
                            error = null
                        )
                    }
                    is Result.Success -> {
                        _uiState.value = _uiState.value.copy(
                            fitnessClass = result.data,
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
}
