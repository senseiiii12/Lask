package dev.alexmester.newsfeed.impl.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.alexmester.ui.R
import dev.alexmester.ui.desing_system.LaskColors
import dev.alexmester.ui.desing_system.LaskTypography
import dev.alexmester.ui.uitext.UiText
import dev.alexmester.utils.locale.DateFormatter

@Composable
internal fun NewsFeedOfflineBanner(
    lastCachedAt: Long?,
    modifier: Modifier = Modifier,
) {
    val timeText: UiText = remember(lastCachedAt) {
        if (lastCachedAt != null) {
            val formatted = DateFormatter.formatCachedAtDate(lastCachedAt)
            UiText.StringResource(R.string.offline_banner_with_time, arrayOf(formatted))
        } else {
            UiText.StringResource(R.string.offline_banner)
        }
    }

    Text(
        text = timeText.asString(),
        style = MaterialTheme.LaskTypography.footnoteSemiBold,
        color = MaterialTheme.LaskColors.textPrimary,
        textAlign = TextAlign.Center,
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.LaskColors.error)
            .padding(8.dp),
    )
}

