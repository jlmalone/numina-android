# Task: Add Gamification UI

> **IMPORTANT**: Check for `.task-gamification-ui-completed` before starting.
> If it exists, respond: "✅ This task has already been implemented."
> **When finished**, create `.task-gamification-ui-completed` file.

## Overview
Add comprehensive gamification UI including achievements, challenges, leaderboards, and user statistics dashboards.

## Requirements

### 1. Data Models

**File**: `app/src/main/kotlin/com/numina/domain/model/Achievement.kt`
```kotlin
package com.numina.domain.model

data class Achievement(
    val id: String,
    val name: String,
    val description: String,
    val iconUrl: String? = null,
    val category: AchievementCategory,
    val tier: AchievementTier,
    val points: Int,
    val requirementType: String,
    val requirementValue: Int,
    val createdAt: String
)

enum class AchievementCategory {
    ATTENDANCE, SOCIAL, FITNESS, MILESTONE
}

enum class AchievementTier {
    BRONZE, SILVER, GOLD, PLATINUM
}

data class UserAchievement(
    val id: String,
    val userId: String,
    val achievement: Achievement,
    val unlockedAt: String,
    val progress: Int
)

data class Challenge(
    val id: String,
    val title: String,
    val description: String,
    val imageUrl: String? = null,
    val challengeType: ChallengeType,
    val goalValue: Int,
    val goalMetric: String,
    val startDate: String,
    val endDate: String,
    val pointsReward: Int,
    val maxParticipants: Int? = null,
    val currentParticipants: Int = 0,
    val isActive: Boolean,
    val hasJoined: Boolean = false
)

enum class ChallengeType {
    ATTENDANCE, STREAK, SOCIAL, DISTANCE, CALORIES
}

data class UserStats(
    val userId: String,
    val totalClassesAttended: Int,
    val currentStreakDays: Int,
    val longestStreakDays: Int,
    val lastClassDate: String? = null,
    val totalPoints: Int,
    val totalAchievements: Int,
    val totalChallengesCompleted: Int,
    val favoriteActivityType: String? = null,
    val totalDistanceKm: Double,
    val totalCaloriesBurned: Int
)

data class LeaderboardEntry(
    val userId: String,
    val userName: String,
    val userPhotoUrl: String? = null,
    val rank: Int,
    val value: Int,
    val isCurrentUser: Boolean = false
)
```

### 2. API Service

**File**: `app/src/main/kotlin/com/numina/data/remote/GamificationApi.kt`
```kotlin
package com.numina.data.remote

import com.numina.domain.model.*
import retrofit2.Response
import retrofit2.http.*

interface GamificationApi {
    @GET("gamification/achievements")
    suspend fun getAchievements(): Response<List<Achievement>>

    @GET("gamification/achievements/user")
    suspend fun getUserAchievements(): Response<List<UserAchievement>>

    @GET("gamification/challenges")
    suspend fun getActiveChallenges(): Response<List<Challenge>>

    @GET("gamification/challenges/{id}")
    suspend fun getChallengeDetails(@Path("id") id: String): Response<Challenge>

    @POST("gamification/challenges/{id}/join")
    suspend fun joinChallenge(@Path("id") id: String): Response<Unit>

    @GET("gamification/challenges/user")
    suspend fun getUserChallenges(): Response<List<Challenge>>

    @GET("gamification/stats")
    suspend fun getUserStats(): Response<UserStats>

    @GET("gamification/leaderboards/{type}/{period}")
    suspend fun getLeaderboard(
        @Path("type") type: String,
        @Path("period") period: String,
        @Query("limit") limit: Int = 100
    ): Response<List<LeaderboardEntry>>
}
```

### 3. Achievements Screen

**File**: `app/src/main/kotlin/com/numina/ui/achievements/AchievementsScreen.kt`
```kotlin
package com.numina.ui.achievements

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.numina.domain.model.*

@Composable
fun AchievementsScreen(
    viewModel: AchievementsViewModel = hiltViewModel()
) {
    val achievements by viewModel.achievements.collectAsState()
    val userAchievements by viewModel.userAchievements.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Achievements",
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = "${userAchievements.size}/${achievements.size} Unlocked",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Group by category
        AchievementCategory.values().forEach { category ->
            val categoryAchievements = achievements.filter { it.category == category }
            if (categoryAchievements.isNotEmpty()) {
                item {
                    Text(
                        text = category.name.lowercase().replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                items(categoryAchievements) { achievement ->
                    val userAchievement = userAchievements.find { it.achievement.id == achievement.id }
                    AchievementCard(
                        achievement = achievement,
                        isUnlocked = userAchievement != null,
                        progress = userAchievement?.progress ?: 0
                    )
                }
            }
        }
    }
}

@Composable
fun AchievementCard(
    achievement: Achievement,
    isUnlocked: Boolean,
    progress: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isUnlocked) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = achievement.name,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = achievement.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Tier badge
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = when (achievement.tier) {
                        AchievementTier.BRONZE -> Color(0xFFCD7F32)
                        AchievementTier.SILVER -> Color(0xFFC0C0C0)
                        AchievementTier.GOLD -> Color(0xFFFFD700)
                        AchievementTier.PLATINUM -> Color(0xFFE5E4E2)
                    }
                ) {
                    Text(
                        text = achievement.tier.name,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }

            if (!isUnlocked && progress > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = progress / 100f,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "$progress%",
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Row(
                modifier = Modifier.padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "+${achievement.points} points",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
```

### 4. Challenges Screen

**File**: `app/src/main/kotlin/com/numina/ui/challenges/ChallengesScreen.kt`
```kotlin
package com.numina.ui.challenges

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

@Composable
fun ChallengesScreen(
    viewModel: ChallengesViewModel = hiltViewModel()
) {
    val challenges by viewModel.challenges.collectAsState()
    val userChallenges by viewModel.userChallenges.collectAsState()

    var selectedTab by remember { mutableStateOf(0) }

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = selectedTab) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Available") }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("My Challenges") }
            )
        }

        when (selectedTab) {
            0 -> AvailableChallengesList(
                challenges = challenges,
                onJoinChallenge = { viewModel.joinChallenge(it) }
            )
            1 -> MyChallengesList(
                challenges = userChallenges
            )
        }
    }
}

@Composable
fun ChallengeCard(
    challenge: Challenge,
    onJoin: () -> Unit,
    showJoinButton: Boolean = true
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = challenge.title,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = challenge.description,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Goal: ${challenge.goalValue} ${challenge.goalMetric}")
                Text("${challenge.pointsReward} points")
            }

            Text(
                text = "${challenge.startDate} - ${challenge.endDate}",
                style = MaterialTheme.typography.labelSmall
            )

            if (showJoinButton && !challenge.hasJoined) {
                Button(
                    onClick = onJoin,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text("Join Challenge")
                }
            }
        }
    }
}
```

### 5. Leaderboards Screen

**File**: `app/src/main/kotlin/com/numina/ui/leaderboards/LeaderboardsScreen.kt`
```kotlin
package com.numina.ui.leaderboards

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

@Composable
fun LeaderboardsScreen(
    viewModel: LeaderboardsViewModel = hiltViewModel()
) {
    var selectedType by remember { mutableStateOf("POINTS") }
    var selectedPeriod by remember { mutableStateOf("ALL_TIME") }

    val leaderboard by viewModel.leaderboard.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        // Type selector
        ScrollableTabRow(selectedTabIndex = 0) {
            listOf("POINTS", "STREAK", "CLASSES", "CHALLENGES").forEachIndexed { index, type ->
                Tab(
                    selected = selectedType == type,
                    onClick = {
                        selectedType = type
                        viewModel.loadLeaderboard(type, selectedPeriod)
                    },
                    text = { Text(type) }
                )
            }
        }

        // Period selector
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf("WEEKLY", "MONTHLY", "ALL_TIME").forEach { period ->
                FilterChip(
                    selected = selectedPeriod == period,
                    onClick = {
                        selectedPeriod = period
                        viewModel.loadLeaderboard(selectedType, period)
                    },
                    label = { Text(period) }
                )
            }
        }

        // Leaderboard list
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(leaderboard) { entry ->
                LeaderboardEntryCard(entry)
            }
        }
    }
}

@Composable
fun LeaderboardEntryCard(entry: LeaderboardEntry) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (entry.isCurrentUser) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rank
            Text(
                text = "#${entry.rank}",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.width(48.dp)
            )

            // User info
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar placeholder
                Surface(
                    modifier = Modifier.size(40.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {}

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = entry.userName,
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            // Value
            Text(
                text = entry.value.toString(),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
```

### 6. Stats Dashboard

**File**: `app/src/main/kotlin/com/numina/ui/stats/StatsScreen.kt`
```kotlin
package com.numina.ui.stats

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*

@Composable
fun StatsScreen(
    viewModel: StatsViewModel = hiltViewModel()
) {
    val stats by viewModel.stats.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Your Stats",
                style = MaterialTheme.typography.headlineMedium
            )
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    StatRow("Total Points", stats.totalPoints.toString())
                    StatRow("Classes Attended", stats.totalClassesAttended.toString())
                    StatRow("Current Streak", "${stats.currentStreakDays} days")
                    StatRow("Longest Streak", "${stats.longestStreakDays} days")
                    StatRow("Achievements", stats.totalAchievements.toString())
                    StatRow("Challenges Completed", stats.totalChallengesCompleted.toString())
                    StatRow("Distance", "${stats.totalDistanceKm} km")
                    StatRow("Calories Burned", stats.totalCaloriesBurned.toString())
                }
            }
        }
    }
}

@Composable
fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
```

### 7. Navigation Updates

Add to `Screen.kt`:
```kotlin
object Achievements : Screen("achievements", "Achievements", Icons.Default.Star)
object Challenges : Screen("challenges", "Challenges", Icons.Default.Flag)
object Leaderboards : Screen("leaderboards", "Leaderboards", Icons.Default.Leaderboard)
object Stats : Screen("stats", "Stats", Icons.Default.Analytics)
```

Add to `NavGraph.kt`:
```kotlin
composable(Screen.Achievements.route) {
    AchievementsScreen()
}
composable(Screen.Challenges.route) {
    ChallengesScreen()
}
composable(Screen.Leaderboards.route) {
    LeaderboardsScreen()
}
composable(Screen.Stats.route) {
    StatsScreen()
}
```

## Completion Checklist
- [ ] All data models implemented
- [ ] API service created
- [ ] Achievements screen complete
- [ ] Challenges screen complete
- [ ] Leaderboards screen complete
- [ ] Stats dashboard complete
- [ ] Navigation updated
- [ ] All screens properly themed
- [ ] `.task-gamification-ui-completed` file created

## Success Criteria
1. ✅ Complete gamification UI across all features
2. ✅ Achievements display with progress
3. ✅ Challenges browsing and joining functional
4. ✅ Leaderboards with filtering working
5. ✅ Stats dashboard comprehensive
