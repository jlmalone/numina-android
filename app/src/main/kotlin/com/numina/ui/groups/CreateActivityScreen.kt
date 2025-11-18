package com.numina.ui.groups

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.numina.data.models.CreateActivityRequest
import com.numina.data.models.GroupActivity
import com.numina.data.repository.GroupRepository
import com.numina.data.repository.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

data class CreateActivityUiState(
    val title: String = "",
    val description: String = "",
    val dateTime: String = "",
    val location: String = "",
    val fitnessClassId: String = "",
    val isCreating: Boolean = false,
    val error: String? = null,
    val createdActivity: GroupActivity? = null
)

@HiltViewModel
class CreateActivityViewModel @Inject constructor(
    private val groupRepository: GroupRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val groupId: String = checkNotNull(savedStateHandle["groupId"])

    private val _uiState = MutableStateFlow(CreateActivityUiState())
    val uiState: StateFlow<CreateActivityUiState> = _uiState.asStateFlow()

    fun updateTitle(title: String) {
        _uiState.value = _uiState.value.copy(title = title)
    }

    fun updateDescription(description: String) {
        _uiState.value = _uiState.value.copy(description = description)
    }

    fun updateDateTime(dateTime: String) {
        _uiState.value = _uiState.value.copy(dateTime = dateTime)
    }

    fun updateLocation(location: String) {
        _uiState.value = _uiState.value.copy(location = location)
    }

    fun updateFitnessClassId(fitnessClassId: String) {
        _uiState.value = _uiState.value.copy(fitnessClassId = fitnessClassId)
    }

    fun createActivity() {
        val state = _uiState.value

        if (state.title.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Title is required")
            return
        }

        if (state.description.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Description is required")
            return
        }

        if (state.dateTime.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Date and time are required")
            return
        }

        val request = CreateActivityRequest(
            title = state.title,
            description = state.description,
            dateTime = state.dateTime,
            location = state.location.takeIf { it.isNotBlank() },
            fitnessClassId = state.fitnessClassId.takeIf { it.isNotBlank() }
        )

        viewModelScope.launch {
            groupRepository.createActivity(groupId, request).collect { result ->
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
                            createdActivity = result.data
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateActivityScreen(
    uiState: CreateActivityUiState,
    onUpdateTitle: (String) -> Unit,
    onUpdateDescription: (String) -> Unit,
    onUpdateDateTime: (String) -> Unit,
    onUpdateLocation: (String) -> Unit,
    onUpdateFitnessClassId: (String) -> Unit,
    onCreate: () -> Unit,
    onBack: () -> Unit,
    onNavigateToActivity: (String) -> Unit
) {
    // Navigate to created activity
    LaunchedEffect(uiState.createdActivity) {
        uiState.createdActivity?.let { activity ->
            onNavigateToActivity(activity.id)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Activity") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Schedule a group activity",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            OutlinedTextField(
                value = uiState.title,
                onValueChange = onUpdateTitle,
                label = { Text("Activity Title") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = uiState.description,
                onValueChange = onUpdateDescription,
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5
            )

            OutlinedTextField(
                value = uiState.dateTime,
                onValueChange = onUpdateDateTime,
                label = { Text("Date & Time (ISO 8601 format)") },
                placeholder = { Text("2024-01-15T10:00:00Z") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                supportingText = {
                    Text("Format: YYYY-MM-DDTHH:MM:SSZ")
                }
            )

            OutlinedTextField(
                value = uiState.location,
                onValueChange = onUpdateLocation,
                label = { Text("Location (optional)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = uiState.fitnessClassId,
                onValueChange = onUpdateFitnessClassId,
                label = { Text("Link to Fitness Class (optional)") },
                placeholder = { Text("Class ID") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.weight(1f))

            uiState.error?.let { error ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = error,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }

            Button(
                onClick = onCreate,
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isCreating
            ) {
                if (uiState.isCreating) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Creating...")
                } else {
                    Text("Create Activity")
                }
            }
        }
    }
}
