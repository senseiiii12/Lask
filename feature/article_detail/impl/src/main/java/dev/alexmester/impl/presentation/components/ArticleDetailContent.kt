package dev.alexmester.impl.presentation.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.alexmester.models.news.NewsArticle
import dev.alexmester.ui.desing_system.LaskColors
import dev.alexmester.ui.desing_system.LaskTypography
import dev.alexmester.ui.transition.sharedElementIfAvailable
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter

@Composable
fun ArticleDetailContent(
    article: NewsArticle,
    bottomPadding: Dp,
    onScrollThresholdReached: () -> Unit,
) {
    val scrollState = rememberScrollState()

    LaunchedEffect(scrollState) {
        snapshotFlow {
            val max = scrollState.maxValue
            if (max <= 0) return@snapshotFlow true
            scrollState.value.toFloat() / max >= 0.5f
        }
            .distinctUntilChanged()
            .filter { it }
            .collect {
                onScrollThresholdReached()
            }
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.LaskColors.backgroundPrimary)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
        ) {
            ArticleDetailHeaderImage(
                image = article.image,
                articleId = article.id
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
            ) {
                article.category?.let { category ->
                    Text(
                        text = category.replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.LaskTypography.footnoteSemiBold,
                        color = MaterialTheme.LaskColors.brand_blue,
                        modifier = Modifier.padding(bottom = 8.dp),
                    )
                }
                Text(
                    modifier = Modifier.sharedElementIfAvailable(key = "title_${article.id}"),
                    text = article.title,
                    style = MaterialTheme.LaskTypography.h4,
                    color = MaterialTheme.LaskColors.textPrimary,
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    AuthorAvatar(
                        char = article.authors.firstOrNull()?.firstOrNull()?.toString()
                        ?: "?"
                    )
                    Column {
                        Text(
                            text = article.authors.firstOrNull() ?: "Unknown",
                            style = MaterialTheme.LaskTypography.body2SemiBold,
                            color = MaterialTheme.LaskColors.textPrimary,
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = article.publishDate,
                            style = MaterialTheme.LaskTypography.footnote,
                            color = MaterialTheme.LaskColors.textSecondary,
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                val displayText = article.text ?: article.summary ?: "Текст статьи недоступен"
                Text(
                    text = displayText,
                    style = MaterialTheme.LaskTypography.body1,
                    color = MaterialTheme.LaskColors.textPrimary,
                )

                article.sentiment?.let { sentiment ->
                    Spacer(modifier = Modifier.height(16.dp))
                    SentimentRow(sentiment = sentiment)
                }
                Spacer(modifier = Modifier.height(bottomPadding + 16.dp))
            }
        }
    }
}