package com.numina.ui.reviews.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.StarHalf
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.ceil
import kotlin.math.floor

@Composable
fun StarRating(
    rating: Double,
    modifier: Modifier = Modifier,
    starSize: Dp = 20.dp,
    color: Color = Color(0xFFFFB300)
) {
    Row(modifier = modifier) {
        for (i in 1..5) {
            val icon = when {
                rating >= i -> Icons.Default.Star
                rating >= i - 0.5 -> Icons.Default.StarHalf
                else -> Icons.Default.StarBorder
            }
            Icon(
                imageVector = icon,
                contentDescription = "Star $i",
                tint = color,
                modifier = Modifier.size(starSize)
            )
        }
    }
}

@Composable
fun InteractiveStarRating(
    rating: Int,
    onRatingChanged: (Int) -> Unit,
    modifier: Modifier = Modifier,
    starSize: Dp = 32.dp,
    color: Color = Color(0xFFFFB300)
) {
    Row(modifier = modifier) {
        for (i in 1..5) {
            val icon = if (i <= rating) Icons.Default.Star else Icons.Default.StarBorder
            Icon(
                imageVector = icon,
                contentDescription = "Star $i",
                tint = color,
                modifier = Modifier
                    .size(starSize)
                    .clickable { onRatingChanged(i) }
            )
        }
    }
}
