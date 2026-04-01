package dev.alexmester.impl.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.alexmester.impl.presentation.ArticleDetailIntent
import dev.alexmester.impl.presentation.ArticleDetailState
import dev.alexmester.ui.desing_system.LaskColors
import dev.alexmester.ui.desing_system.LaskPalette
import dev.alexmester.ui.desing_system.LaskTypography

@Composable
fun ArticleDetailContent(
    state: ArticleDetailState.Content,
    onIntent: (ArticleDetailIntent) -> Unit,
) {
    val article = state.article

    Box(modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.LaskColors.backgroundPrimary)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            ArticleDetailHeaderImage(image = article.image)
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
                    lineHeight = MaterialTheme.LaskTypography.body1.fontSize * 1.6,
                )

                // Sentiment
                article.sentiment?.let { sentiment ->
                    Spacer(modifier = Modifier.height(16.dp))
                    val (sentimentLabel, sentimentColor) = when {
                        sentiment > 0.1 -> "Позитивная тональность" to LaskPalette.Sentiment_Positive
                        sentiment < -0.1 -> "Негативная тональность" to LaskPalette.Sentiment_Negative
                        else -> "Нейтральная тональность" to LaskPalette.Sentiment_Neutral
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Text("●", color = sentimentColor, style = MaterialTheme.LaskTypography.footnote)
                        Text(
                            text = sentimentLabel,
                            style = MaterialTheme.LaskTypography.footnote,
                            color = MaterialTheme.LaskColors.textSecondary,
                        )
                    }
                }

                // Bottom padding for bottom bar
                Spacer(modifier = Modifier.height(96.dp))
            }
        }

        // ── Top status bar insets spacer ───────────────────────────────
        // (hero image goes under status bar via edge-to-edge)

        // ── Bottom bar ────────────────────────────────────────────────
        ArticleDetailBottomBar(
            state = state,
            onIntent = onIntent,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}