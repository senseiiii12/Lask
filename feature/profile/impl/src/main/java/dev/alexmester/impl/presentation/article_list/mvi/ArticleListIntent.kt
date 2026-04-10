package dev.alexmester.impl.presentation.article_list.mvi

sealed class ArticleListIntent {
    data class SelectCategory(val category: String?) : ArticleListIntent()
    data object Back : ArticleListIntent()
    data class ArticleClick(val articleId: Long, val articleUrl: String) : ArticleListIntent()
}