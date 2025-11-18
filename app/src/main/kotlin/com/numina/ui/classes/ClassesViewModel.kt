package com.numina.ui.classes

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

data class ClassFilters(
    val dateFrom: String? = null,
    val dateTo: String? = null,
    val type: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val radius: Int? = null,
    val priceMin: Double? = null,
    val priceMax: Double? = null,
    val intensityMin: Int? = null,
    val intensityMax: Int? = null
)

data class ClassesUiState(
    val classes: List<FitnessClass> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val filters: ClassFilters = ClassFilters()
)

@HiltViewModel
class ClassesViewModel @Inject constructor(
    private val classRepository: ClassRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ClassesUiState())
    val uiState: StateFlow<ClassesUiState> = _uiState.asStateFlow()

    init {
        loadClasses()
        loadCachedClasses()
    }

    private fun loadCachedClasses() {
        viewModelScope.launch {
            classRepository.getCachedClasses().collect { cached ->
                if (_uiState.value.classes.isEmpty()) {
                    _uiState.value = _uiState.value.copy(classes = cached)
                }
            }
        }
    }

    fun loadClasses(refresh: Boolean = false) {
        viewModelScope.launch {
            val filters = _uiState.value.filters
            classRepository.fetchClasses(
                dateFrom = filters.dateFrom,
                dateTo = filters.dateTo,
                type = filters.type,
                latitude = filters.latitude,
                longitude = filters.longitude,
                radius = filters.radius,
                priceMin = filters.priceMin,
                priceMax = filters.priceMax,
                intensityMin = filters.intensityMin,
                intensityMax = filters.intensityMax
            ).collect { result ->
                when (result) {
                    is Result.Loading -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = true,
                            error = null
                        )
                    }
                    is Result.Success -> {
                        _uiState.value = _uiState.value.copy(
                            classes = result.data.classes,
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

    fun applyFilters(filters: ClassFilters) {
        _uiState.value = _uiState.value.copy(filters = filters)
        loadClasses()
    }

    fun clearFilters() {
        _uiState.value = _uiState.value.copy(filters = ClassFilters())
        loadClasses()
    }
}
