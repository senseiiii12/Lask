package dev.alexmester.ui.components.shimmer

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.alexmester.ui.desing_system.LaskColors
import androidx.compose.material3.MaterialTheme

@Composable
fun ShimmerTextBlock(
    modifier: Modifier = Modifier,
    lineCount: Int = 6,
    lineHeight: Dp = 14.dp,
    lineSpacing: Dp = 10.dp,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val shimmerX by infiniteTransition.animateFloat(
        initialValue = -1000f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "shimmerX",
    )

    val shimmerBrush = Brush.linearGradient(
        colors = listOf(
            MaterialTheme.LaskColors.brand_blue10,
            MaterialTheme.LaskColors.brand_blue.copy(alpha = 0.3f),
            MaterialTheme.LaskColors.brand_blue10,
        ),
        start = Offset(shimmerX, 0f),
        end = Offset(shimmerX + 600f, 0f),
    )

    androidx.compose.foundation.layout.Column(modifier = modifier) {
        repeat(lineCount) { index ->
            val widthFraction = if (index == lineCount - 1) 0.6f else 1f
            Box(
                modifier = Modifier
                    .fillMaxWidth(widthFraction)
                    .height(lineHeight)
                    .padding(bottom = lineSpacing)
                    .background(shimmerBrush, RoundedCornerShape(4.dp))
            )
        }
    }
}