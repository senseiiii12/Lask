package dev.alexmester.ui.components.buttons

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import dev.alexmester.ui.R
import dev.alexmester.ui.desing_system.LaskColors

@Composable
fun LaskTranslateButton(
    modifier: Modifier = Modifier,
    isTranslated: Boolean,
    isEnabled: Boolean,
    onClick: () -> Unit,
) {
    val tint by animateColorAsState(
        targetValue = when {
            !isEnabled -> MaterialTheme.LaskColors.textSecondary.copy(alpha = 0.4f)
            isTranslated -> MaterialTheme.LaskColors.brand_blue
            else -> MaterialTheme.LaskColors.textPrimary
        },
        label = "translateColor",
    )

    IconButton(
        onClick = onClick,
        enabled = isEnabled,
        modifier = modifier,
    ) {
        Icon(
            imageVector = Icons.Default.Translate,
            contentDescription = if (isTranslated) "Revert translation" else "Translate",
            tint = tint,
            modifier = Modifier.size(24.dp),
        )
    }
}