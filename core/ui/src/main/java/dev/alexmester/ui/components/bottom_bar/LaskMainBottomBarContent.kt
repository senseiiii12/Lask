package dev.alexmester.ui.components.bottom_bar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.alexmester.ui.desing_system.LaskColors
import dev.alexmester.ui.desing_system.LaskTypography

@Composable
fun LaskMainBottomBarContent(item: LaskMainBottomBarItem) {

    val backgroundColor = animateColorAsState(
        targetValue = if (item.isSelected) MaterialTheme.LaskColors.textPrimary
        else Color.Transparent,
    )

    val iconColor = if (item.isSelected) MaterialTheme.LaskColors.backgroundPrimary
    else MaterialTheme.LaskColors.textPrimary

    val textColor = if (item.isSelected) MaterialTheme.LaskColors.backgroundPrimary
    else MaterialTheme.LaskColors.textPrimary

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(24.dp))
            .drawBehind{
                drawRect(backgroundColor.value)
            }
            .clickable { item.onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {

        Icon(
            imageVector = item.icon,
            contentDescription = item.title,
            tint = iconColor
        )

        AnimatedVisibility(
            visible = item.isSelected,
            enter = fadeIn() + expandHorizontally(),
            exit = fadeOut() + shrinkHorizontally()
        ) {
            Text(
                text = item.title,
                style = MaterialTheme.LaskTypography.footnoteSemiBold,
                color = textColor,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}