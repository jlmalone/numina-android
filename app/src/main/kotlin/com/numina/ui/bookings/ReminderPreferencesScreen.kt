package com.numina.ui.bookings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.numina.ui.components.ErrorScreen
import com.numina.ui.components.LoadingScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderPreferencesScreen(
    onNavigateBack: () -> Unit,
    viewModel: ReminderPreferencesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reminder Preferences") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        when {
            uiState.isLoading && uiState.preferences == null -> {
                LoadingScreen(modifier = Modifier.padding(padding))
            }
            uiState.error != null && uiState.preferences == null -> {
                ErrorScreen(
                    message = uiState.error,
                    onRetry = { viewModel.loadPreferences() },
                    modifier = Modifier.padding(padding)
                )
            }
            uiState.preferences != null -> {
                val prefs = uiState.preferences!!
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Notification Settings",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Card {
                        Column(modifier = Modifier.padding(16.dp)) {
                            PreferenceSwitch(
                                title = "Email Reminders",
                                description = "Receive reminder emails before classes",
                                checked = prefs.emailRemindersEnabled,
                                onCheckedChange = { viewModel.toggleEmailReminders() }
                            )

                            Divider(modifier = Modifier.padding(vertical = 8.dp))

                            PreferenceSwitch(
                                title = "Push Notifications",
                                description = "Receive push notifications before classes",
                                checked = prefs.pushRemindersEnabled,
                                onCheckedChange = { viewModel.togglePushReminders() }
                            )
                        }
                    }

                    Text(
                        text = "Reminder Timing",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Card {
                        Column(modifier = Modifier.padding(16.dp)) {
                            PreferenceSwitch(
                                title = "1 Hour Before",
                                description = "Send reminder 1 hour before class",
                                checked = prefs.reminder1hEnabled,
                                onCheckedChange = { viewModel.toggle1hReminder() }
                            )

                            Divider(modifier = Modifier.padding(vertical = 8.dp))

                            PreferenceSwitch(
                                title = "24 Hours Before",
                                description = "Send reminder 24 hours before class",
                                checked = prefs.reminder24hEnabled,
                                onCheckedChange = { viewModel.toggle24hReminder() }
                            )
                        }
                    }

                    Text(
                        text = "Quiet Hours",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Card {
                        Column(modifier = Modifier.padding(16.dp)) {
                            PreferenceSwitch(
                                title = "Enable Quiet Hours",
                                description = "Don't send notifications during quiet hours",
                                checked = prefs.quietHoursEnabled,
                                onCheckedChange = { viewModel.toggleQuietHours() }
                            )

                            if (prefs.quietHoursEnabled) {
                                Spacer(modifier = Modifier.height(16.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(
                                            text = "Start",
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                        Text(
                                            text = prefs.quietHoursStart ?: "22:00",
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                    }

                                    Column {
                                        Text(
                                            text = "End",
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                        Text(
                                            text = prefs.quietHoursEnd ?: "08:00",
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                    }
                                }
                            }
                        }
                    }

                    if (uiState.isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }

                    if (uiState.saveSuccess) {
                        Text(
                            text = "Preferences saved successfully",
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PreferenceSwitch(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}
