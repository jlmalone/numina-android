package com.numina.ui.social

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
    uiState: UserProfileUiState,
    onBack: () -> Unit,
    onFollowToggle: () -> Unit,
    onActivityClick: (String) -> Unit,
    onFollowersClick: () -> Unit,
    onFollowingClick: () -> Unit,
    onLoadMoreActivities: () -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        when {
            uiState.isLoading && uiState.profile == null -> {
                LoadingState()
            }
            uiState.error != null && uiState.profile == null -> {
                ErrorState(message = uiState.error)
            }
            uiState.profile != null -> {
                LazyColumn(
                    state = listState,
                    modifier = modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    // Profile header
                    item {
                        ProfileHeader(
                            profile = uiState.profile,
                            isFollowing = uiState.isFollowing,
                            onFollowToggle = onFollowToggle,
                            onFollowersClick = onFollowersClick,
                            onFollowingClick = onFollowingClick
                        )
                    }

                    // Stats card
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        StatsCard(
                            followersCount = uiState.profile.followersCount,
                            followingCount = uiState.profile.followingCount,
                            workoutsCount = uiState.profile.workoutsCount,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }

                    // Activities section header
                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "Recent Activities",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // Activities list
                    items(
                        items = uiState.activities,
                        key = { it.id }
                    ) { activity ->
                        ActivityFeedItem(
                            activity = activity,
                            onLikeClick = { /* User can't like from this screen */ },
                            onCommentClick = onActivityClick,
                            onUserClick = { /* Already on user profile */ }
                        )
                    }

                    if (uiState.isLoadingMore) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }

                    if (uiState.activities.isEmpty()) {
                        item {
                            EmptyActivitiesState()
                        }
                    }
                }

                // Load more when reaching the end
                LaunchedEffect(listState) {
                    snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
                        .collect { lastVisibleIndex ->
                            if (lastVisibleIndex != null &&
                                lastVisibleIndex >= uiState.activities.size + 2 && // +2 for header items
                                uiState.hasMore &&
                                !uiState.isLoadingMore
                            ) {
                                onLoadMoreActivities()
                            }
                        }
                }
            }
        }
    }
}

@Composable
private fun ProfileHeader(
    profile: com.numina.data.models.UserProfile,
    isFollowing: Boolean,
    onFollowToggle: () -> Unit,
    onFollowersClick: () -> Unit,
    onFollowingClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Profile picture
        Surface(
            modifier = Modifier.size(100.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = profile.name.firstOrNull()?.toString()?.uppercase() ?: "?",
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Name
        Text(
            text = profile.name,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        // Bio
        if (profile.bio != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = profile.bio,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Fitness level
        if (profile.fitnessLevel != null) {
            Spacer(modifier = Modifier.height(8.dp))
            AssistChip(
                onClick = { },
                label = { Text("Fitness Level ${profile.fitnessLevel}") },
                leadingIcon = {
                    Icon(
                        Icons.Default.FitnessCenter,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            )
        }

        // Interests
        if (!profile.fitnessInterests.isNullOrEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                profile.fitnessInterests.take(3).forEach { interest ->
                    AssistChip(
                        onClick = { },
                        label = { Text(interest) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Follow button
        Button(
            onClick = onFollowToggle,
            modifier = Modifier.fillMaxWidth(),
            colors = if (isFollowing) {
                ButtonDefaults.outlinedButtonColors()
            } else {
                ButtonDefaults.buttonColors()
            }
        ) {
            Text(if (isFollowing) "Following" else "Follow")
        }

        if (profile.isMutual) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "You both follow each other",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorState(message: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(48.dp)
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun EmptyActivitiesState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "No activities yet",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}
