package dev.alexmester.impl.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import dev.alexmester.impl.presentation.components.body.ArticleDetailBody
import dev.alexmester.impl.presentation.components.common.rememberScrollThreshold
import dev.alexmester.impl.presentation.components.header.ArticleDetailHeaderImage
import dev.alexmester.impl.presentation.mvi.TranslationState
import dev.alexmester.models.news.NewsArticle
import dev.alexmester.ui.desing_system.LaskColors
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter

@Composable
internal fun ArticleDetailContent(
    article: NewsArticle,
    bottomPadding: Dp,
    translatedTitle: String?,
    translatedText: String?,
    translationState: TranslationState,
    onScrollThresholdReached: () -> Unit,
) {
    val scrollState = rememberScrollState()

    rememberScrollThreshold(
        scrollState = scrollState,
        onThresholdReached = onScrollThresholdReached,
    )

    Box(
        modifier = Modifier
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
                articleId = article.id,
            )
            ArticleDetailBody(
                article = article,
                bottomPadding = bottomPadding,
                translatedTitle = translatedTitle,
                translatedText = translatedText,
                translationState = translationState,
            )
        }
    }
}