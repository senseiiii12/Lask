package dev.alexmester.impl.presentation

sealed class ArticleDetailIntent {
    data object Back : ArticleDetailIntent()
    data object Clap : ArticleDetailIntent()
    data object ToggleBookmark : ArticleDetailIntent()
    data object Share : ArticleDetailIntent()
}