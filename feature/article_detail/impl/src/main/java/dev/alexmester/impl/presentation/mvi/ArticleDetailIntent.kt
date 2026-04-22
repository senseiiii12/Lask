package dev.alexmester.impl.presentation.mvi

sealed class ArticleDetailIntent {
    data object Back : ArticleDetailIntent()
    data object Clap : ArticleDetailIntent()
    data object ToggleBookmark : ArticleDetailIntent()
    data object Share : ArticleDetailIntent()
    data object TimeThresholdReached : ArticleDetailIntent()
    data object ScrollThresholdReached : ArticleDetailIntent()
    data object Translate : ArticleDetailIntent()
    data object RevertTranslation : ArticleDetailIntent()
}