package com.numina.ui.notifications

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.numina.data.models.Notification
import com.numina.ui.components.*
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    uiState: NotificationsUiState,
    onNotificationClick: (String) -> Unit,
    onRefresh: () -> Unit,
    onSettingsClick: () -> Unit,
    onMarkAllRead: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    var isRefreshing by remember { mutableStateOf(false) }
    val pullToRefreshState = rememberPullToRefreshState()

    LaunchedEffect(uiState.isLoading) {
        if (!uiState.isLoading) {
            isRefreshing = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Notifications")
                        if (uiState.unreadCount > 0) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Badge {
                                Text(uiState.unreadCount.toString())
                            }
                        }
                    }
                },
                actions = {
                    if (uiState.unreadCount > 0) {
                        IconButton(onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onMarkAllRead()
                        }) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = "Mark all as read"
                            )
                        }
                    }
                    IconButton(onClick = onSettingsClick) {
                        Icon(Icons.Default.Settings, contentDescription = "Notification Settings")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .nestedScroll(pullToRefreshState.nestedScrollConnection)
        ) {
            when {
                uiState.isLoading && uiState.notifications.isEmpty() -> {
                    // Show skeleton loading
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(6) {
                            SkeletonNotificationItem()
                        }
                    }
                }
                uiState.error != null && uiState.notifications.isEmpty() -> {
                    ErrorScreen(
                        message = uiState.error,
                        onRetry = onRefresh
                    )
                }
                uiState.notifications.isEmpty() -> {
                    EmptyStateView(
                        message = "No notifications yet",
                        icon = Icons.Default.NotificationsNone,
                        actionLabel = "Refresh",
                        onAction = onRefresh
                    )
                }
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        itemsIndexed(
                            items = uiState.notifications,
                            key = { _, item -> item.id }
                        ) { index, notification ->
                            AnimatedNotificationItem(
                                notification = notification,
                                index = index,
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    onNotificationClick(notification.id)
                                }
                            )
                        }
                    }
                }
            }

            // Pull to refresh
            if (pullToRefreshState.isRefreshing) {
                LaunchedEffect(true) {
                    isRefreshing = true
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onRefresh()
                }
            }

            LaunchedEffect(isRefreshing) {
                if (isRefreshing) {
                    pullToRefreshState.startRefresh()
                } else {
                    pullToRefreshState.endRefresh()
                }
            }

            PullToRefreshContainer(
                state = pullToRefreshState,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}

/**
 * Notification item with fade-in animation
 */
@Composable
fun AnimatedNotificationItem(
    notification: Notification,
    index: Int,
    onClick: () -> Unit
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(index * 50L)
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(300)) +
                expandVertically(animationSpec = tween(300))
    ) {
        NotificationItem(
            notification = notification,
            onClick = onClick
        )
    }
}

@Composable
fun NotificationItem(
    notification: Notification,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (notification.isRead) 0.dp else 2.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (notification.isRead) {
                MaterialTheme.colorScheme.surface
            } else {
                MaterialTheme.colorScheme.secondaryContainer
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = getNotificationTypeLabel(notification.type),
                        style = MaterialTheme.typography.labelSmall,
                        color = getNotificationTypeColor(notification.type),
                        fontWeight = FontWeight.Medium
                    )
                    if (!notification.isRead) {
                        Badge(
                            containerColor = MaterialTheme.colorScheme.primary
                        ) {
                            Text("New", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = notification.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = if (notification.isRead) FontWeight.Normal else FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = notification.body,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = formatNotificationTime(notification.createdAt),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun getNotificationTypeLabel(type: String): String {
    return when (type) {
        "message" -> "MESSAGE"
        "match" -> "NEW MATCH"
        "group" -> "GROUP"
        "reminder" -> "REMINDER"
        else -> type.uppercase()
    }
}

@Composable
private fun getNotificationTypeColor(type: String): androidx.compose.ui.graphics.Color {
    return when (type) {
        "message" -> MaterialTheme.colorScheme.primary
        "match" -> MaterialTheme.colorScheme.tertiary
        "group" -> MaterialTheme.colorScheme.secondary
        "reminder" -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
}

private fun formatNotificationTime(timestamp: String): String {
    // Simple formatting - in production, use a proper date library
    return try {
        // For now, just return the timestamp
        // In production, you'd format this properly with java.time or similar
        timestamp.take(16).replace("T", " ")
    } catch (e: Exception) {
        timestamp
    }
}
