package com.numina.ui.reviews.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.numina.data.models.RatingBreakdown

@Composable
fun RatingSummary(
    averageRating: Double,
    totalReviews: Int,
    ratingBreakdown: RatingBreakdown,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Average rating
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = String.format("%.1f", averageRating),
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                StarRating(rating = averageRating)
                Text(
                    text = "$totalReviews reviews",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Rating breakdown
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                RatingBar(5, ratingBreakdown.fiveStars, totalReviews)
                RatingBar(4, ratingBreakdown.fourStars, totalReviews)
                RatingBar(3, ratingBreakdown.threeStars, totalReviews)
                RatingBar(2, ratingBreakdown.twoStars, totalReviews)
                RatingBar(1, ratingBreakdown.oneStar, totalReviews)
            }
        }
    }
}

@Composable
private fun RatingBar(
    stars: Int,
    count: Int,
    total: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$stars",
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.width(12.dp)
        )
        LinearProgressIndicator(
            progress = if (total > 0) count.toFloat() / total else 0f,
            modifier = Modifier
                .weight(1f)
                .height(8.dp),
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.width(32.dp)
        )
    }
}
