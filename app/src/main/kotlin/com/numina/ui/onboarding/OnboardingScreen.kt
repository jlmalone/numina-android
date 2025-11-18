package com.numina.ui.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.numina.data.models.Availability
import com.numina.data.models.Location
import com.numina.ui.components.NuminaButton
import com.numina.ui.components.NuminaOutlinedButton

@Composable
fun OnboardingScreen(
    uiState: OnboardingState,
    onUpdateName: (String) -> Unit,
    onUpdateBio: (String) -> Unit,
    onToggleInterest: (String) -> Unit,
    onUpdateFitnessLevel: (Int) -> Unit,
    onUpdateLocation: (Location) -> Unit,
    onUpdateAvailability: (List<Availability>) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit,
    onComplete: () -> Unit,
    onNavigateToClasses: () -> Unit
) {
    LaunchedEffect(uiState.isComplete) {
        if (uiState.isComplete) {
            onNavigateToClasses()
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            // Progress indicator
            LinearProgressIndicator(
                progress = (uiState.currentStep + 1) / 5f,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Step content
            when (uiState.currentStep) {
                0 -> BasicInfoStep(
                    name = uiState.name,
                    bio = uiState.bio,
                    onNameChange = onUpdateName,
                    onBioChange = onUpdateBio
                )
                1 -> FitnessInterestsStep(
                    selectedInterests = uiState.fitnessInterests,
                    onToggleInterest = onToggleInterest
                )
                2 -> FitnessLevelStep(
                    level = uiState.fitnessLevel,
                    onLevelChange = onUpdateFitnessLevel
                )
                3 -> LocationStep(
                    location = uiState.location,
                    onLocationChange = onUpdateLocation
                )
                4 -> AvailabilityStep(
                    availability = uiState.availability,
                    onAvailabilityChange = onUpdateAvailability
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Navigation buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (uiState.currentStep > 0) {
                    NuminaOutlinedButton(
                        text = "Back",
                        onClick = onBack,
                        modifier = Modifier.weight(1f)
                    )
                }

                NuminaButton(
                    text = if (uiState.currentStep == 4) "Finish" else "Next",
                    onClick = {
                        if (uiState.currentStep == 4) {
                            onComplete()
                        } else {
                            onNext()
                        }
                    },
                    loading = uiState.isLoading,
                    modifier = Modifier.weight(1f)
                )
            }

            if (uiState.error != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = uiState.error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun BasicInfoStep(
    name: String,
    bio: String,
    onNameChange: (String) -> Unit,
    onBioChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Tell us about yourself",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = bio,
            onValueChange = onBioChange,
            label = { Text("Bio (Optional)") },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 4,
            minLines = 3
        )
    }
}

@Composable
fun FitnessInterestsStep(
    selectedInterests: List<String>,
    onToggleInterest: (String) -> Unit
) {
    val interests = listOf("Yoga", "HIIT", "Spin", "Pilates", "Boxing", "Running", "Strength Training", "Dance")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "What are your fitness interests?",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        interests.chunked(2).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                row.forEach { interest ->
                    FilterChip(
                        selected = selectedInterests.contains(interest),
                        onClick = { onToggleInterest(interest) },
                        label = { Text(interest) },
                        modifier = Modifier.weight(1f)
                    )
                }
                if (row.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun FitnessLevelStep(
    level: Int,
    onLevelChange: (Int) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "What's your fitness level?",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Level: $level",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        Slider(
            value = level.toFloat(),
            onValueChange = { onLevelChange(it.toInt()) },
            valueRange = 1f..10f,
            steps = 8
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Beginner", style = MaterialTheme.typography.bodySmall)
            Text("Advanced", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun LocationStep(
    location: Location?,
    onLocationChange: (Location) -> Unit
) {
    var address by remember { mutableStateOf(location?.address ?: "") }
    var latitude by remember { mutableStateOf(location?.latitude?.toString() ?: "") }
    var longitude by remember { mutableStateOf(location?.longitude?.toString() ?: "") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Where are you located?",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = address,
            onValueChange = { address = it },
            label = { Text("Address") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = latitude,
            onValueChange = { latitude = it },
            label = { Text("Latitude") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = longitude,
            onValueChange = { longitude = it },
            label = { Text("Longitude") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val lat = latitude.toDoubleOrNull() ?: 0.0
                val lon = longitude.toDoubleOrNull() ?: 0.0
                onLocationChange(Location(lat, lon, address))
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Location")
        }
    }
}

@Composable
fun AvailabilityStep(
    availability: List<Availability>,
    onAvailabilityChange: (List<Availability>) -> Unit
) {
    val days = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
    val timeSlots = listOf("Morning", "Afternoon", "Evening")

    var selectedDays by remember { mutableStateOf(availability.map { it.day }) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "When are you available?",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        days.forEach { day ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .toggleable(
                        value = selectedDays.contains(day),
                        onValueChange = {
                            selectedDays = if (it) {
                                selectedDays + day
                            } else {
                                selectedDays - day
                            }
                            // Update availability with all time slots for selected days
                            val newAvailability = selectedDays.map { selectedDay ->
                                Availability(selectedDay, timeSlots)
                            }
                            onAvailabilityChange(newAvailability)
                        }
                    )
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = selectedDays.contains(day),
                    onCheckedChange = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(day)
            }
        }
    }
}
