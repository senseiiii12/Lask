package dev.alexmester.impl.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.alexmester.ui.R
import dev.alexmester.ui.desing_system.LaskColors
import dev.alexmester.ui.desing_system.LaskTypography
import dev.alexmester.utils.locale.LocaleUtils.countryCodeToFlagEmoji
import dev.alexmester.utils.locale.LocaleUtils.countryCodeToFullCountryName
import dev.alexmester.utils.locale.LocaleUtils.languageCodeToFullLanguageName

@Composable
internal fun NewsFeedEmptyScreen(
    country: String,
    language: String,
    modifier: Modifier = Modifier,
) {
    val countryFlag = countryCodeToFlagEmoji(country)
    val countryName = countryCodeToFullCountryName(country)
    val languageName = languageCodeToFullLanguageName(language)

    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = countryFlag,
            style = MaterialTheme.LaskTypography.h1,
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.error_news_empty),
            style = MaterialTheme.LaskTypography.h4,
            color = MaterialTheme.LaskColors.textPrimary,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(
                R.string.locale_incompatible_empty_message,
                countryName,languageName
            ),
            style = MaterialTheme.LaskTypography.body2,
            color = MaterialTheme.LaskColors.textSecondary,
            textAlign = TextAlign.Center,
        )
    }
}