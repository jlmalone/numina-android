package com.numina.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * Error types for different error scenarios
 */
enum class ErrorType {
    NETWORK,
    SERVER,
    AUTH,
    NOT_FOUND,
    GENERIC
}

/**
 * Enhanced error view with retry callback and error type support
 */
@Composable
fun ErrorView(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    errorType: ErrorType = ErrorType.GENERIC,
    title: String? = null
) {
    val (icon, defaultTitle) = when (errorType) {
        ErrorType.NETWORK -> Icons.Default.CloudOff to "Network Error"
        ErrorType.SERVER -> Icons.Default.ErrorOutline to "Server Error"
        ErrorType.AUTH -> Icons.Default.Lock to "Authentication Error"
        ErrorType.NOT_FOUND -> Icons.Default.SearchOff to "Not Found"
        ErrorType.GENERIC -> Icons.Default.ErrorOutline to "Error"
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.error
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = title ?: defaultTitle,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onRetry,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Retry")
        }
    }
}

/**
 * Network error view specifically for offline scenarios
 */
@Composable
fun NetworkErrorView(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    message: String = "No internet connection. Please check your network settings and try again."
) {
    ErrorView(
        message = message,
        onRetry = onRetry,
        modifier = modifier,
        errorType = ErrorType.NETWORK
    )
}

/**
 * Inline error card for errors that appear within content
 */
@Composable
fun ErrorCard(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    errorType: ErrorType = ErrorType.GENERIC
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = when (errorType) {
                    ErrorType.NETWORK -> Icons.Default.CloudOff
                    ErrorType.SERVER -> Icons.Default.ErrorOutline
                    ErrorType.AUTH -> Icons.Default.Lock
                    ErrorType.NOT_FOUND -> Icons.Default.SearchOff
                    ErrorType.GENERIC -> Icons.Default.ErrorOutline
                },
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(12.dp))

            TextButton(onClick = onRetry) {
                Text("Retry")
            }
        }
    }
}

/**
 * Empty state view for empty lists
 */
@Composable
fun EmptyStateView(
    message: String,
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Default.Info,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        if (actionLabel != null && onAction != null) {
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(onClick = onAction) {
                Text(actionLabel)
            }
        }
    }
}

/**
 * Helper function to determine error type from error message
 */
fun getErrorType(errorMessage: String): ErrorType {
    return when {
        errorMessage.contains("network", ignoreCase = true) ||
        errorMessage.contains("connection", ignoreCase = true) ||
        errorMessage.contains("offline", ignoreCase = true) -> ErrorType.NETWORK

        errorMessage.contains("401", ignoreCase = true) ||
        errorMessage.contains("unauthorized", ignoreCase = true) ||
        errorMessage.contains("authentication", ignoreCase = true) -> ErrorType.AUTH

        errorMessage.contains("404", ignoreCase = true) ||
        errorMessage.contains("not found", ignoreCase = true) -> ErrorType.NOT_FOUND

        errorMessage.contains("500", ignoreCase = true) ||
        errorMessage.contains("503", ignoreCase = true) ||
        errorMessage.contains("server", ignoreCase = true) -> ErrorType.SERVER

        else -> ErrorType.GENERIC
    }
}
