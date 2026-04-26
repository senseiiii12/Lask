package dev.alexmester.impl.presentation.mvi

import dev.alexmester.models.locale.SupportedLocales
import dev.alexmester.models.news.NewsArticle
import dev.alexmester.ui.uitext.UiText

sealed interface ArticleDetailState {
    data object Loading : ArticleDetailState
    data class Error(val message: UiText) : ArticleDetailState
    data class Content(
        val article: NewsArticle,
        val isBookmarked: Boolean = false,
        val clapCount: Int = 0,
        val translationState: TranslationState = TranslationState.Idle,
        val translatedTitle: String? = null,
        val translatedText: String? = null,
        val autoTranslateLanguage: String = SupportedLocales.FALLBACK_LANGUAGE,
    ) : ArticleDetailState
}

sealed interface TranslationState {
    data object Idle : TranslationState
    data object Loading : TranslationState
    data object Translated : TranslationState
}

val ArticleDetailState.contentOrNull: ArticleDetailState.Content?
    get() = this as? ArticleDetailState.Content

val ArticleDetailState.isContent: Boolean
    get() = this is ArticleDetailState.Content

val ArticleDetailState.IsTranslated: Boolean
    get() = this is ArticleDetailState.Content
            && this.translationState is TranslationState.Translated

val ArticleDetailState.IsTranslateLoading: Boolean
    get() = this is ArticleDetailState.Content
            && this.translationState is TranslationState.Loading