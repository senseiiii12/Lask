package dev.alexmester.impl.presentation.components.body

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.alexmester.impl.presentation.mvi.TranslationState
import dev.alexmester.models.news.NewsArticle

@Composable
internal fun ArticleDetailBody(
    article: NewsArticle,
    bottomPadding: Dp,
    translatedTitle: String?,
    translatedText: String?,
    translationState: TranslationState,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp)
    ) {
        ArticleDetailCategory(category = article.category)
        ArticleDetailTitle(
            title = article.title,
            articleId = article.id,
            translatedTitle = translatedTitle,
            translationState = translationState,
        )
        Spacer(modifier = Modifier.height(16.dp))
        ArticleDetailAuthorRow(
            authors = article.authors,
            publishDate = article.publishDate,
            sentiment = article.sentiment
        )
        Spacer(modifier = Modifier.height(24.dp))
        ArticleDetailText(
            text = article.text,
            summary = article.summary,
            translatedText = translatedText,
            translationState = translationState,
        )
        article.video?.let { videoUrl ->
            Spacer(modifier = Modifier.height(24.dp))
            ArticleDetailVideoPlayer(videoUrl = videoUrl)
        }
        Spacer(modifier = Modifier.height(bottomPadding + 16.dp))
    }
}