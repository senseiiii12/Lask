package dev.alexmester.ui.components.list_card.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.alexmester.models.news.NewsArticle
import dev.alexmester.ui.desing_system.LaskColors
import dev.alexmester.ui.desing_system.LaskTypography
import dev.alexmester.ui.shared_transition.sharedElementIfAvailable

@Composable
internal fun ArticleTitle(article: NewsArticle) {
    Text(
        text = article.title,
        style = MaterialTheme.LaskTypography.h5,
        color = MaterialTheme.LaskColors.textPrimary,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier
            .sharedElementIfAvailable(key = "title_${article.id}")
            .padding(bottom = 8.dp),
    )
}