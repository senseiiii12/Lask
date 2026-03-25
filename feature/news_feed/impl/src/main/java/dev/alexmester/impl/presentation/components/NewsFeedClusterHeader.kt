package dev.alexmester.newsfeed.impl.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.alexmester.ui.desing_system.LaskColors
import dev.alexmester.ui.desing_system.LaskTypography

@Composable
fun NewsFeedClusterHeader(
    title: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = title,
        style = MaterialTheme.LaskTypography.body1,
        color = MaterialTheme.LaskColors.textPrimary,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.LaskColors.backgroundSecondary,
                shape = RoundedCornerShape(bottomEnd = 12.dp, bottomStart = 12.dp)
            )
            .padding(horizontal = 16.dp,vertical = 10.dp),
    )
}
