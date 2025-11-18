package com.numina.ui.groups

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateGroupScreen(
    uiState: CreateGroupUiState,
    onUpdateName: (String) -> Unit,
    onUpdateDescription: (String) -> Unit,
    onUpdateCategory: (String) -> Unit,
    onUpdatePrivacy: (String) -> Unit,
    onUpdateCity: (String) -> Unit,
    onUpdateState: (String) -> Unit,
    onUpdateCountry: (String) -> Unit,
    onUpdateMaxMembers: (String) -> Unit,
    onUpdatePhotoUrl: (String) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit,
    onCreate: () -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToGroup: (String) -> Unit
) {
    // Navigate to created group
    LaunchedEffect(uiState.createdGroup) {
        uiState.createdGroup?.let { group ->
            onNavigateToGroup(group.id)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Group") },
                navigationIcon = {
                    IconButton(onClick = if (uiState.currentStep > 0) onBack else onNavigateBack) {
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
            // Progress indicator
            LinearProgressIndicator(
                progress = { (uiState.currentStep + 1) / 3f },
                modifier = Modifier.fillMaxWidth()
            )

            when (uiState.currentStep) {
                0 -> BasicInfoStep(
                    name = uiState.name,
                    description = uiState.description,
                    category = uiState.category,
                    onUpdateName = onUpdateName,
                    onUpdateDescription = onUpdateDescription,
                    onUpdateCategory = onUpdateCategory
                )
                1 -> PrivacyLocationStep(
                    privacy = uiState.privacy,
                    city = uiState.city,
                    state = uiState.state,
                    country = uiState.country,
                    maxMembers = uiState.maxMembers,
                    onUpdatePrivacy = onUpdatePrivacy,
                    onUpdateCity = onUpdateCity,
                    onUpdateState = onUpdateState,
                    onUpdateCountry = onUpdateCountry,
                    onUpdateMaxMembers = onUpdateMaxMembers
                )
                2 -> PhotoStep(
                    photoUrl = uiState.photoUrl,
                    onUpdatePhotoUrl = onUpdatePhotoUrl
                )
            }

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

            if (uiState.currentStep < 2) {
                Button(
                    onClick = onNext,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = when (uiState.currentStep) {
                        0 -> uiState.name.isNotBlank() && uiState.description.isNotBlank()
                        else -> true
                    }
                ) {
                    Text("Next")
                }
            } else {
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
                        Text("Create Group")
                    }
                }
            }
        }
    }
}

@Composable
fun BasicInfoStep(
    name: String,
    description: String,
    category: String,
    onUpdateName: (String) -> Unit,
    onUpdateDescription: (String) -> Unit,
    onUpdateCategory: (String) -> Unit
) {
    Text(
        text = "Basic Information",
        style = MaterialTheme.typography.titleLarge
    )

    OutlinedTextField(
        value = name,
        onValueChange = onUpdateName,
        label = { Text("Group Name") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
    )

    OutlinedTextField(
        value = description,
        onValueChange = onUpdateDescription,
        label = { Text("Description") },
        modifier = Modifier.fillMaxWidth(),
        minLines = 3,
        maxLines = 5
    )

    val categories = listOf("fitness", "yoga", "running", "cycling", "hiking", "sports", "other")
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = category.replaceFirstChar { it.uppercase() },
            onValueChange = {},
            readOnly = true,
            label = { Text("Category") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            categories.forEach { cat ->
                DropdownMenuItem(
                    text = { Text(cat.replaceFirstChar { it.uppercase() }) },
                    onClick = {
                        onUpdateCategory(cat)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun PrivacyLocationStep(
    privacy: String,
    city: String,
    state: String,
    country: String,
    maxMembers: String,
    onUpdatePrivacy: (String) -> Unit,
    onUpdateCity: (String) -> Unit,
    onUpdateState: (String) -> Unit,
    onUpdateCountry: (String) -> Unit,
    onUpdateMaxMembers: (String) -> Unit
) {
    Text(
        text = "Privacy & Location",
        style = MaterialTheme.typography.titleLarge
    )

    val privacyOptions = listOf("public", "private", "invite-only")
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = privacy.replace("-", " ").replaceFirstChar { it.uppercase() },
            onValueChange = {},
            readOnly = true,
            label = { Text("Privacy") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            privacyOptions.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.replace("-", " ").replaceFirstChar { it.uppercase() }) },
                    onClick = {
                        onUpdatePrivacy(option)
                        expanded = false
                    }
                )
            }
        }
    }

    OutlinedTextField(
        value = maxMembers,
        onValueChange = onUpdateMaxMembers,
        label = { Text("Max Members (optional)") },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true
    )

    Text(
        text = "Location (optional)",
        style = MaterialTheme.typography.titleMedium
    )

    OutlinedTextField(
        value = city,
        onValueChange = onUpdateCity,
        label = { Text("City") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
    )

    OutlinedTextField(
        value = state,
        onValueChange = onUpdateState,
        label = { Text("State/Province (optional)") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
    )

    OutlinedTextField(
        value = country,
        onValueChange = onUpdateCountry,
        label = { Text("Country") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
    )
}

@Composable
fun PhotoStep(
    photoUrl: String,
    onUpdatePhotoUrl: (String) -> Unit
) {
    Text(
        text = "Group Photo",
        style = MaterialTheme.typography.titleLarge
    )

    Text(
        text = "Add a photo URL for your group (optional)",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )

    OutlinedTextField(
        value = photoUrl,
        onValueChange = onUpdatePhotoUrl,
        label = { Text("Photo URL") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
    )
}
