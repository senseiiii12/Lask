package dev.alexmester.newsfeed.impl.presentation.feed

import dev.alexmester.models.error.NetworkError
import dev.alexmester.models.news.NewsCluster

sealed interface NewsFeedState {
    data object Loading : NewsFeedState
    data class Error(
        val errorType: NetworkError,
        val isRefreshing: Boolean = false,
        ) : NewsFeedState
    data class Content(
        val clusters: List<NewsCluster>,
        val country: String = "en",
        val lastCachedAt: Long? = null,
        val contentState: ContentState = ContentState.Idle,
    ) : NewsFeedState
    data class Empty(
        val country: String = "en",
        val language: String = "us",
        val isRefreshing: Boolean = false,
    ) : NewsFeedState
}

sealed interface ContentState {
    data object Idle : ContentState
    data object Refreshing : ContentState
    data class Offline(val lastCachedAt: Long?) : ContentState
}


val NewsFeedState.contentOrNull: NewsFeedState.Content?
    get() = this as? NewsFeedState.Content

val NewsFeedState.isContent: Boolean
    get() = this is NewsFeedState.Content

val NewsFeedState.isLoading: Boolean
    get() = this is NewsFeedState.Loading

val NewsFeedState.isError: Boolean
    get() = this is NewsFeedState.Error

val NewsFeedState.isRefreshing: Boolean
    get() = when (this) {
        is NewsFeedState.Content -> this.contentState is ContentState.Refreshing
        is NewsFeedState.Empty -> this.isRefreshing
        is NewsFeedState.Error -> this.isRefreshing
        else -> false
    }

val NewsFeedState.isOffline: Boolean
    get() = this is NewsFeedState.Content && this.contentState is ContentState.Offline