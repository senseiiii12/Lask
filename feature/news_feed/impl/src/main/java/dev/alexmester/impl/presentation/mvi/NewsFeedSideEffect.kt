package dev.alexmester.newsfeed.impl.presentation.feed

import dev.alexmester.models.error.NetworkError


sealed class NewsFeedSideEffect {

    data class NavigateToArticle(
        val articleId: Long,
        val articleUrl: String,
    ) : NewsFeedSideEffect()

    data class ShowError(val message: NetworkError) : NewsFeedSideEffect()
}
