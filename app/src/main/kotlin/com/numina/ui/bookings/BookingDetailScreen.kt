package com.numina.ui.bookings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.numina.ui.bookings.components.BookingStatusBadge
import com.numina.ui.bookings.components.InfoChip
import com.numina.ui.components.ErrorScreen
import com.numina.ui.components.LoadingScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingDetailScreen(
    onNavigateBack: () -> Unit,
    viewModel: BookingDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showCancelDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Booking Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        when {
            uiState.isLoading && uiState.booking == null -> {
                LoadingScreen(modifier = Modifier.padding(padding))
            }
            uiState.error != null && uiState.booking == null -> {
                ErrorScreen(
                    message = uiState.error,
                    onRetry = { viewModel.loadBooking() },
                    modifier = Modifier.padding(padding)
                )
            }
            uiState.booking != null -> {
                val booking = uiState.booking!!
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Status badge
                    BookingStatusBadge(
                        attended = booking.attended,
                        cancelled = booking.cancelled
                    )

                    // Class details
                    Text(
                        text = booking.fitnessClass.name,
                        style = MaterialTheme.typography.headlineMedium
                    )

                    Text(
                        text = booking.fitnessClass.type,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Divider()

                    // Time and duration
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        InfoChip(
                            icon = Icons.Default.DateRange,
                            text = booking.fitnessClass.dateTime
                        )
                        InfoChip(
                            icon = Icons.Default.Schedule,
                            text = "${booking.fitnessClass.duration} min"
                        )
                    }

                    // Location
                    Card {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.LocationOn, contentDescription = null)
                                Column {
                                    Text(
                                        text = booking.fitnessClass.location.name,
                                        style = MaterialTheme.typography.titleSmall
                                    )
                                    Text(
                                        text = booking.fitnessClass.location.address,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    // Trainer
                    Card {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.Person, contentDescription = null)
                                Column {
                                    Text(
                                        text = booking.fitnessClass.trainer.name,
                                        style = MaterialTheme.typography.titleSmall
                                    )
                                    booking.fitnessClass.trainer.bio?.let { bio ->
                                        Text(
                                            text = bio,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Notes
                    if (!booking.notes.isNullOrEmpty()) {
                        Card {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Notes",
                                    style = MaterialTheme.typography.titleSmall
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = booking.notes,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }

                    // Actions
                    if (!booking.cancelled && !booking.attended) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { showCancelDialog = true },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Cancel Booking")
                            }
                            Button(
                                onClick = { viewModel.markAttended() },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Mark Attended")
                            }
                        }
                    }
                }
            }
        }
    }

    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = { Text("Cancel Booking") },
            text = { Text("Are you sure you want to cancel this booking?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.cancelBooking()
                    showCancelDialog = false
                }) {
                    Text("Yes, Cancel")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDialog = false }) {
                    Text("No")
                }
            }
        )
    }

    if (uiState.actionSuccess) {
        LaunchedEffect(Unit) {
            onNavigateBack()
        }
    }
}
