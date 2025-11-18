package com.numina.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Modifier extension to add shimmer animation effect to any composable
 */
fun Modifier.shimmer(): Modifier = composed {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1200,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translate"
    )

    val shimmerColors = listOf(
        Color.LightGray.copy(alpha = 0.6f),
        Color.LightGray.copy(alpha = 0.2f),
        Color.LightGray.copy(alpha = 0.6f),
    )

    background(
        brush = Brush.linearGradient(
            colors = shimmerColors,
            start = Offset(translateAnim - 1000f, translateAnim - 1000f),
            end = Offset(translateAnim, translateAnim)
        )
    )
}

/**
 * Basic skeleton box with shimmer effect
 */
@Composable
fun SkeletonBox(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(4.dp)
) {
    Box(
        modifier = modifier
            .clip(shape)
            .shimmer()
    )
}

/**
 * Skeleton circle (for avatars, profile pics)
 */
@Composable
fun SkeletonCircle(
    size: Dp,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .shimmer()
    )
}

/**
 * Skeleton text line
 */
@Composable
fun SkeletonText(
    width: Dp = 120.dp,
    height: Dp = 16.dp,
    modifier: Modifier = Modifier
) {
    SkeletonBox(
        modifier = modifier
            .width(width)
            .height(height)
    )
}

/**
 * Skeleton class card for class list screen
 */
@Composable
fun SkeletonClassCard(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Title
            SkeletonText(width = 200.dp, height = 20.dp)
            Spacer(modifier = Modifier.height(8.dp))
            // Instructor
            SkeletonText(width = 120.dp, height = 14.dp)
            Spacer(modifier = Modifier.height(8.dp))
            // Details row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SkeletonText(width = 80.dp, height = 14.dp)
                SkeletonText(width = 80.dp, height = 14.dp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            // Rating
            SkeletonText(width = 100.dp, height = 14.dp)
        }
    }
}

/**
 * Skeleton message/conversation item
 */
@Composable
fun SkeletonConversationItem(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar
        SkeletonCircle(size = 48.dp)
        Spacer(modifier = Modifier.width(12.dp))
        Column(
            modifier = Modifier.weight(1f)
        ) {
            // Name
            SkeletonText(width = 120.dp, height = 16.dp)
            Spacer(modifier = Modifier.height(8.dp))
            // Last message
            SkeletonText(width = 200.dp, height = 14.dp)
        }
        // Time
        SkeletonText(width = 40.dp, height = 12.dp)
    }
}

/**
 * Skeleton group card
 */
@Composable
fun SkeletonGroupCard(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Group icon/image
                SkeletonCircle(size = 56.dp)
                Spacer(modifier = Modifier.width(12.dp))
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    // Group name
                    SkeletonText(width = 150.dp, height = 18.dp)
                    Spacer(modifier = Modifier.height(8.dp))
                    // Member count
                    SkeletonText(width = 80.dp, height = 14.dp)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            // Description
            SkeletonText(width = 250.dp, height = 14.dp)
            Spacer(modifier = Modifier.height(4.dp))
            SkeletonText(width = 180.dp, height = 14.dp)
        }
    }
}

/**
 * Skeleton activity feed item
 */
@Composable
fun SkeletonActivityItem(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // User info
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                SkeletonCircle(size = 40.dp)
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    SkeletonText(width = 120.dp, height = 16.dp)
                    Spacer(modifier = Modifier.height(4.dp))
                    SkeletonText(width = 80.dp, height = 12.dp)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            // Activity title
            SkeletonText(width = 180.dp, height = 18.dp)
            Spacer(modifier = Modifier.height(8.dp))
            // Activity details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SkeletonText(width = 70.dp, height = 14.dp)
                SkeletonText(width = 70.dp, height = 14.dp)
                SkeletonText(width = 70.dp, height = 14.dp)
            }
            Spacer(modifier = Modifier.height(12.dp))
            // Like/comment bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SkeletonText(width = 60.dp, height = 14.dp)
                SkeletonText(width = 60.dp, height = 14.dp)
            }
        }
    }
}

/**
 * Skeleton notification item
 */
@Composable
fun SkeletonNotificationItem(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Icon
        SkeletonCircle(size = 40.dp)
        Spacer(modifier = Modifier.width(12.dp))
        Column(
            modifier = Modifier.weight(1f)
        ) {
            // Title
            SkeletonText(width = 200.dp, height = 16.dp)
            Spacer(modifier = Modifier.height(8.dp))
            // Message
            SkeletonText(width = 250.dp, height = 14.dp)
            Spacer(modifier = Modifier.height(4.dp))
            SkeletonText(width = 180.dp, height = 14.dp)
            Spacer(modifier = Modifier.height(8.dp))
            // Time
            SkeletonText(width = 60.dp, height = 12.dp)
        }
    }
}

/**
 * Skeleton review item
 */
@Composable
fun SkeletonReviewItem(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // User info and rating
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SkeletonCircle(size = 32.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                    SkeletonText(width = 100.dp, height = 16.dp)
                }
                SkeletonText(width = 60.dp, height = 14.dp)
            }
            Spacer(modifier = Modifier.height(12.dp))
            // Review text
            SkeletonText(width = 280.dp, height = 14.dp)
            Spacer(modifier = Modifier.height(4.dp))
            SkeletonText(width = 250.dp, height = 14.dp)
            Spacer(modifier = Modifier.height(4.dp))
            SkeletonText(width = 150.dp, height = 14.dp)
            Spacer(modifier = Modifier.height(8.dp))
            // Date
            SkeletonText(width = 80.dp, height = 12.dp)
        }
    }
}

/**
 * Skeleton profile header
 */
@Composable
fun SkeletonProfileHeader(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Profile picture
        SkeletonCircle(size = 100.dp)
        Spacer(modifier = Modifier.height(16.dp))
        // Name
        SkeletonText(width = 150.dp, height = 24.dp)
        Spacer(modifier = Modifier.height(8.dp))
        // Bio
        SkeletonText(width = 200.dp, height = 14.dp)
        Spacer(modifier = Modifier.height(16.dp))
        // Stats
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                SkeletonText(width = 40.dp, height = 20.dp)
                Spacer(modifier = Modifier.height(4.dp))
                SkeletonText(width = 60.dp, height = 12.dp)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                SkeletonText(width = 40.dp, height = 20.dp)
                Spacer(modifier = Modifier.height(4.dp))
                SkeletonText(width = 60.dp, height = 12.dp)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                SkeletonText(width = 40.dp, height = 20.dp)
                Spacer(modifier = Modifier.height(4.dp))
                SkeletonText(width = 60.dp, height = 12.dp)
            }
        }
    }
}

/**
 * Generic skeleton list for any screen
 */
@Composable
fun SkeletonList(
    itemCount: Int = 5,
    itemContent: @Composable (Int) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        repeat(itemCount) { index ->
            itemContent(index)
        }
    }
}
