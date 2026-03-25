package dev.alexmester.newsfeed.impl.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.alexmester.ui.desing_system.LaskColors
import dev.alexmester.ui.desing_system.LaskTypography
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun NewsFeedOfflineBanner(
    lastCachedAt: Long?,
    modifier: Modifier = Modifier,
) {
    val timeText = lastCachedAt?.let {
        val sdf = SimpleDateFormat("HH:mm dd.MM.yyyy", Locale.getDefault())
        "Offline · updated at ${sdf.format(Date(it))}"
    } ?: "Offline"

    Text(
        text = timeText,
        style = MaterialTheme.LaskTypography.footnoteSemiBold,
        color = MaterialTheme.LaskColors.textPrimary,
        textAlign = TextAlign.Center,
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.LaskColors.warning)
            .padding(8.dp),
    )
}
