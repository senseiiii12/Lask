package dev.alexmester.impl.presentation.article_list.mvi

sealed class ArticleListSideEffect {
    data object NavigateBack: ArticleListSideEffect()
    data class NavigateToArticle(
        val articleId: Long,
        val articleUrl: String
    ): ArticleListSideEffect()
}