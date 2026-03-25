package dev.alexmester.newsfeed.impl.presentation.feed

sealed class NewsFeedSideEffect {

    data class NavigateToArticle(
        val articleId: Long,
        val articleUrl: String,
    ) : NewsFeedSideEffect()

    data class ShowError(val message: String) : NewsFeedSideEffect()
}
