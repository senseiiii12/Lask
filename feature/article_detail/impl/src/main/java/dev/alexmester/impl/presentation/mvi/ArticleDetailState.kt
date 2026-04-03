package dev.alexmester.impl.presentation.mvi

import dev.alexmester.models.news.NewsArticle
import dev.alexmester.ui.uitext.UiText

sealed interface ArticleDetailState {
    data object Loading : ArticleDetailState
    data class Error(val message: UiText) : ArticleDetailState
    data class Content(
        val article: NewsArticle,
        val isBookmarked: Boolean = false,
        val clapCount: Int = 0,
        val isClapAnimating: Boolean = false,
    ) : ArticleDetailState
}

val ArticleDetailState.contentOrNull: ArticleDetailState.Content?
    get() = this as? ArticleDetailState.Content
