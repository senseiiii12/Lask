package dev.alexmester.ui.components.buttons

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import dev.alexmester.ui.R
import dev.alexmester.ui.desing_system.LaskColors
import dev.alexmester.ui.desing_system.LaskPalette
import dev.alexmester.ui.desing_system.LaskTypography

@Composable
fun LaskClapButton(
    count: Int,
    onClick: () -> Unit,
) {
    val isAnimateClap by remember {
        derivedStateOf { count > 0 }
    }
    val scale by animateFloatAsState(
        targetValue = if (isAnimateClap) 1.25f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "clapScale",
    )
    val icon = if (isAnimateClap) R.drawable.ic_clap_filled else R.drawable.ic_clap_outline
    val tint by animateColorAsState(
        targetValue = if (isAnimateClap) LaskPalette.Brand_Blue
        else MaterialTheme.LaskColors.textPrimary,
        label = "clapColor",
    )

    Row(
        modifier = Modifier.animateContentSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        IconButton(onClick = onClick) {
            Icon(
                imageVector = ImageVector.vectorResource(icon),
                contentDescription = null,
                tint = tint,
                modifier = Modifier
                    .size(24.dp)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    },
            )
        }
        if (isAnimateClap) {
            Text(
                text = count.toString(),
                style = MaterialTheme.LaskTypography.button1,
                color = MaterialTheme.LaskColors.textSecondary,
            )
        }
    }
}