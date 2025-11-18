package com.numina.ui.bookings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.numina.ui.bookings.components.AchievementBadge
import com.numina.ui.bookings.components.StatsCard
import com.numina.ui.bookings.components.StreakDisplay
import com.numina.ui.components.ErrorScreen
import com.numina.ui.components.LoadingScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceStatsScreen(
    viewModel: AttendanceStatsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Stats") }
            )
        }
    ) { padding ->
        when {
            uiState.isLoading -> {
                LoadingScreen(modifier = Modifier.padding(padding))
            }
            uiState.error != null -> {
                ErrorScreen(
                    message = uiState.error,
                    onRetry = { viewModel.refresh() },
                    modifier = Modifier.padding(padding)
                )
            }
            uiState.stats != null && uiState.streak != null -> {
                val stats = uiState.stats!!
                val streak = uiState.streak!!

                Column(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // Streak display
                    StreakDisplay(streak = streak)

                    Text(
                        text = "Overview",
                        style = MaterialTheme.typography.titleLarge
                    )

                    // Stats cards
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatsCard(
                            title = "Total Classes",
                            value = stats.totalClassesAttended.toString(),
                            icon = Icons.Default.FitnessCenter,
                            modifier = Modifier.weight(1f)
                        )
                        StatsCard(
                            title = "Attendance Rate",
                            value = "${(stats.attendanceRate * 100).toInt()}%",
                            icon = Icons.Default.TrendingUp,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Classes by type
                    Text(
                        text = "Classes by Type",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Card {
                        Column(modifier = Modifier.padding(16.dp)) {
                            stats.classesByType.forEach { (type, count) ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = type,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    Text(
                                        text = count.toString(),
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                if (stats.classesByType.keys.last() != type) {
                                    Divider()
                                }
                            }
                        }
                    }

                    // Favorite trainer
                    stats.favoriteTrainer?.let { trainer ->
                        Text(
                            text = "Favorite Trainer",
                            style = MaterialTheme.typography.titleMedium
                        )

                        Card {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = trainer.trainerName,
                                        style = MaterialTheme.typography.titleSmall
                                    )
                                    Text(
                                        text = "${trainer.classesAttended} classes together",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }

                    // Achievements
                    if (stats.achievements.isNotEmpty()) {
                        Text(
                            text = "Achievements",
                            style = MaterialTheme.typography.titleMedium
                        )

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(stats.achievements) { achievement ->
                                AchievementBadge(achievement = achievement)
                            }
                        }
                    }

                    // Monthly attendance
                    if (stats.monthlyAttendance.isNotEmpty()) {
                        Text(
                            text = "Monthly Attendance",
                            style = MaterialTheme.typography.titleMedium
                        )

                        Card {
                            Column(modifier = Modifier.padding(16.dp)) {
                                stats.monthlyAttendance.takeLast(6).forEach { month ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = month.yearMonth,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        Text(
                                            text = "${month.classesAttended}/${month.classesBooked}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    if (stats.monthlyAttendance.takeLast(6).last() != month) {
                                        Divider()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
