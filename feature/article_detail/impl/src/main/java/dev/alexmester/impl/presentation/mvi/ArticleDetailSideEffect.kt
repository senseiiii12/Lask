package dev.alexmester.impl.presentation.mvi

import dev.alexmester.models.error.NetworkError

sealed class ArticleDetailSideEffect {
    data object NavigateBack : ArticleDetailSideEffect()
    data class ShareUrl(val url: String) : ArticleDetailSideEffect()
    data class ShawBookmarkActionMessage(val isBookmarked: Boolean) : ArticleDetailSideEffect()
    data class ShowError(val errorType: NetworkError) : ArticleDetailSideEffect()
}