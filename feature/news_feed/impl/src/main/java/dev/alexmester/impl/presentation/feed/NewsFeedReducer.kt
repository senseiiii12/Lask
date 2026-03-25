package dev.alexmester.newsfeed.impl.presentation.feed

import dev.alexmester.models.news.NewsCluster

object NewsFeedReducer {

    fun reduce(state: NewsFeedScreenState, intent: NewsFeedIntent): NewsFeedScreenState =
        when (intent) {
            is NewsFeedIntent.Refresh -> when (state) {
                is NewsFeedScreenState.Content -> state.copy(
                    contentState = ContentState.Refreshing,
                )
                else -> state
            }
            else -> state
        }

    fun onClustersLoaded(clusters: List<NewsCluster>, lastCachedAt: Long?): NewsFeedScreenState =
        NewsFeedScreenState.Content(
        clusters = clusters,
        lastCachedAt = lastCachedAt,
        contentState = ContentState.Idle,
    )

    fun onError(state: NewsFeedScreenState, message: String): NewsFeedScreenState =
        when (state) {
            is NewsFeedScreenState.Content -> state.copy(
                contentState = ContentState.Idle,
            )
            else -> NewsFeedScreenState.Error(message)
        }

    fun onOffline(
        clusters: List<NewsCluster>,
        lastCachedAt: Long?,
        message: String,
    ): NewsFeedScreenState = when {
        clusters.isNotEmpty() -> NewsFeedScreenState.Content(
            clusters = clusters,
            lastCachedAt = lastCachedAt,
            contentState = ContentState.Offline(lastCachedAt),
        )
        else -> NewsFeedScreenState.Error(message)
    }
}