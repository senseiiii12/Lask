package dev.alexmester.impl.presentation.profile.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.alexmester.ui.R
import dev.alexmester.ui.desing_system.LaskColors
import dev.alexmester.ui.desing_system.LaskTheme
import dev.alexmester.ui.desing_system.LaskTypography

@Composable
fun ProfileStatisticRow(
    modifier: Modifier = Modifier,
    articleReadCount: Int,
    streakCount: Int,
    levelCount: Int,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = stringResource(R.string.profile_article_read),
                style = MaterialTheme.LaskTypography.body1,
                color = MaterialTheme.LaskColors.textSecondary
            )
            Text(
                text = articleReadCount.toString(),
                style = MaterialTheme.LaskTypography.h4,
                color = MaterialTheme.LaskColors.textPrimary
            )
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = stringResource(R.string.profile_streak),
                style = MaterialTheme.LaskTypography.body1,
                color = MaterialTheme.LaskColors.textSecondary
            )
            Text(
                text = stringResource(R.string.profile_streak_days,streakCount),
                style = MaterialTheme.LaskTypography.h4,
                color = MaterialTheme.LaskColors.textPrimary
            )
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = stringResource(R.string.profile_level),
                style = MaterialTheme.LaskTypography.body1,
                color = MaterialTheme.LaskColors.textSecondary
            )
            Text(
                text = levelCount.toString(),
                style = MaterialTheme.LaskTypography.h4,
                color = MaterialTheme.LaskColors.textPrimary
            )
        }
    }
}

@Preview(name = "Statictic Light", showBackground = true)
@Composable
private fun ProfileStatisticRowPreviewLight() {
    LaskTheme(darkTheme = false) {
        ProfileStatisticRow(
            modifier = Modifier,
            articleReadCount = 320,
            streakCount = 245,
            levelCount = 450
        )
    }
}
@Preview(name = "Statictic Dark", showBackground = true)
@Composable
private fun ProfileStatisticRowPreviewDark() {
    LaskTheme(darkTheme = true) {
        ProfileStatisticRow(
            modifier = Modifier,
            articleReadCount = 320,
            streakCount = 245,
            levelCount = 450
        )
    }
}