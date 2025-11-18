package com.numina.ui.bookings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.numina.ui.bookings.components.BookingCard
import com.numina.ui.components.EmptyState
import com.numina.ui.components.ErrorScreen
import com.numina.ui.components.LoadingScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingsScreen(
    onBookingClick: (String) -> Unit,
    onNavigateToClasses: () -> Unit,
    viewModel: BookingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Bookings") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToClasses
            ) {
                Icon(Icons.Default.Add, contentDescription = "Book a class")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            // Tab selector
            TabRow(
                selectedTabIndex = if (uiState.showUpcoming) 0 else 1
            ) {
                Tab(
                    selected = uiState.showUpcoming,
                    onClick = { if (!uiState.showUpcoming) viewModel.toggleView() },
                    text = { Text("Upcoming") }
                )
                Tab(
                    selected = !uiState.showUpcoming,
                    onClick = { if (uiState.showUpcoming) viewModel.toggleView() },
                    text = { Text("Past") }
                )
            }

            when {
                uiState.isLoading && uiState.bookings.isEmpty() -> {
                    LoadingScreen()
                }
                uiState.error != null && uiState.bookings.isEmpty() -> {
                    ErrorScreen(
                        message = uiState.error,
                        onRetry = { viewModel.loadBookings() }
                    )
                }
                else -> {
                    val displayBookings = if (uiState.showUpcoming) {
                        uiState.upcomingBookings
                    } else {
                        uiState.pastBookings
                    }

                    if (displayBookings.isEmpty()) {
                        EmptyState(
                            message = if (uiState.showUpcoming) {
                                "No upcoming bookings"
                            } else {
                                "No past bookings"
                            }
                        )
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(displayBookings) { booking ->
                                BookingCard(
                                    booking = booking,
                                    onClick = { onBookingClick(booking.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Show error snackbar
    uiState.error?.let { error ->
        LaunchedEffect(error) {
            // Snackbar would be shown here
            viewModel.clearError()
        }
    }
}
