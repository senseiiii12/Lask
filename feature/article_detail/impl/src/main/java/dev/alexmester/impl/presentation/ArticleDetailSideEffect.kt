package dev.alexmester.impl.presentation

sealed class ArticleDetailSideEffect {
    data object NavigateBack : ArticleDetailSideEffect()
    data class ShareUrl(val url: String) : ArticleDetailSideEffect()
    data class ShowSnackbar(val message: String) : ArticleDetailSideEffect()
}