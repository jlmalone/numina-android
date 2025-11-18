package com.numina.ui.social

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FollowingScreen(
    uiState: FollowingUiState,
    onUserClick: (String) -> Unit,
    onFollowClick: (String, Boolean) -> Unit,
    onUnfollowClick: (String) -> Unit,
    onTabChange: (FollowTab) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Connections") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Tabs
            TabRow(
                selectedTabIndex = uiState.selectedTab.ordinal
            ) {
                Tab(
                    selected = uiState.selectedTab == FollowTab.FOLLOWING,
                    onClick = { onTabChange(FollowTab.FOLLOWING) },
                    text = {
                        Text("Following (${uiState.followingList.size})")
                    }
                )
                Tab(
                    selected = uiState.selectedTab == FollowTab.FOLLOWERS,
                    onClick = { onTabChange(FollowTab.FOLLOWERS) },
                    text = {
                        Text("Followers (${uiState.followersList.size})")
                    }
                )
            }

            // Content
            when {
                uiState.isLoading -> {
                    LoadingState()
                }
                uiState.error != null -> {
                    ErrorState(message = uiState.error)
                }
                else -> {
                    val users = when (uiState.selectedTab) {
                        FollowTab.FOLLOWING -> uiState.followingList
                        FollowTab.FOLLOWERS -> uiState.followersList
                    }

                    if (users.isEmpty()) {
                        EmptyState(tab = uiState.selectedTab)
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(
                                items = users,
                                key = { it.id }
                            ) { user ->
                                FollowUserListItem(
                                    user = user,
                                    onUserClick = onUserClick,
                                    onFollowClick = { userId, isFollowing ->
                                        if (uiState.selectedTab == FollowTab.FOLLOWING) {
                                            onUnfollowClick(userId)
                                        } else {
                                            if (isFollowing) {
                                                onUnfollowClick(userId)
                                            } else {
                                                onFollowClick(userId, false)
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
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
private fun EmptyState(tab: FollowTab) {
    val message = when (tab) {
        FollowTab.FOLLOWING -> "You're not following anyone yet"
        FollowTab.FOLLOWERS -> "You don't have any followers yet"
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (tab == FollowTab.FOLLOWING) {
                Text(
                    text = "Discover and follow users to see their activities",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
