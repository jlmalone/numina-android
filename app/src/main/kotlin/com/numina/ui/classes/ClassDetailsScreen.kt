package com.numina.ui.classes

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.numina.data.models.FitnessClass
import com.numina.ui.components.ErrorScreen
import com.numina.ui.components.LoadingScreen
import com.numina.ui.components.NuminaButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassDetailsScreen(
    uiState: ClassDetailsUiState,
    onBack: () -> Unit,
    onRetry: () -> Unit
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Class Details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
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
                    onRetry = onRetry,
                    modifier = Modifier.padding(padding)
                )
            }
            uiState.fitnessClass != null -> {
                ClassDetailsContent(
                    fitnessClass = uiState.fitnessClass,
                    onBookClass = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uiState.fitnessClass.bookingUrl))
                        context.startActivity(intent)
                    },
                    modifier = Modifier.padding(padding)
                )
            }
        }
    }
}

@Composable
fun ClassDetailsContent(
    fitnessClass: FitnessClass,
    onBookClass: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Class Header
        Text(
            text = fitnessClass.name,
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AssistChip(
                onClick = { },
                label = { Text(fitnessClass.type) }
            )
            Text(
                text = "$${fitnessClass.price} ${fitnessClass.currency}",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Description
        Text(
            text = "Description",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = fitnessClass.description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Class Info
        InfoRow(label = "Duration", value = "${fitnessClass.duration} minutes")
        InfoRow(label = "Intensity", value = "${fitnessClass.intensity}/10")
        InfoRow(label = "Date & Time", value = fitnessClass.dateTime)

        if (fitnessClass.spotsAvailable != null) {
            InfoRow(
                label = "Availability",
                value = "${fitnessClass.spotsAvailable}/${fitnessClass.capacity ?: 0} spots available"
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Trainer Info
        Text(
            text = "Trainer",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = fitnessClass.trainer.name,
            style = MaterialTheme.typography.bodyLarge
        )
        if (fitnessClass.trainer.bio != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = fitnessClass.trainer.bio,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        if (!fitnessClass.trainer.specialties.isNullOrEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                fitnessClass.trainer.specialties.take(3).forEach { specialty ->
                    AssistChip(
                        onClick = { },
                        label = { Text(specialty) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Location
        Text(
            text = "Location",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = fitnessClass.location.name,
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = fitnessClass.location.address,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Action Buttons
        NuminaButton(
            text = "Book on ${fitnessClass.provider}",
            onClick = onBookClass
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = { /* Future feature */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text("Find Workout Partner")
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
