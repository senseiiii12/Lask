package dev.alexmester.ui.components.list_card.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.alexmester.models.news.NewsArticle
import dev.alexmester.ui.components.sentiment.LaskSentimentDot
import dev.alexmester.ui.desing_system.LaskColors
import dev.alexmester.ui.desing_system.LaskTypography
import dev.alexmester.utils.locale.DateFormatter
import dev.alexmester.utils.locale.BuildLocale
import dev.alexmester.utils.locale.countryCodeToFlagEmoji

@Composable
internal fun ArticleMeta(article: NewsArticle) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            article.sourceCountry?.let { countryCode ->
                Text(
                    text = countryCodeToFlagEmoji(countryCode),
                    style = MaterialTheme.LaskTypography.footnote
                )
                Text(
                    text = BuildLocale.countryCodeToFullCountryName(countryCode),
                    style = MaterialTheme.LaskTypography.footnote,
                    color = MaterialTheme.LaskColors.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            article.category?.let {
                Text(
                    text = it.replaceFirstChar { c -> c.uppercase() },
                    style = MaterialTheme.LaskTypography.footnote,
                    color = MaterialTheme.LaskColors.informative,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            article.sentiment?.let { LaskSentimentDot(it) }
            Text(
                text = DateFormatter.formatPublishDate(article.publishDate),
                style = MaterialTheme.LaskTypography.footnote,
                color = MaterialTheme.LaskColors.textSecondary,
            )
            article.authors.firstOrNull()?.let { author ->
                Text(
                    text = "· $author",
                    style = MaterialTheme.LaskTypography.footnote,
                    color = MaterialTheme.LaskColors.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}