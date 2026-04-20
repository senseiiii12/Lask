package dev.alexmester.newsfeed.impl.presentation.feed

import dev.alexmester.models.error.NetworkError
import dev.alexmester.models.news.NewsCluster

object NewsFeedReducer {

    fun reduce(state: NewsFeedState, intent: NewsFeedIntent): NewsFeedState =
        when (intent) {
            is NewsFeedIntent.Refresh -> when (state) {
                is NewsFeedState.Content -> state.copy(
                    contentState = ContentState.Refreshing,
                )
                is NewsFeedState.Error -> state.copy(isRefreshing = true)
                is NewsFeedState.Empty -> state.copy(isRefreshing = true)
                else -> state
            }
            else -> state
        }

    fun onClustersLoaded(
        clusters: List<NewsCluster>,
        lastCachedAt: Long?,
        country: String,
    ): NewsFeedState =
        NewsFeedState.Content(
            clusters = clusters,
            country = country,
            lastCachedAt = lastCachedAt,
            contentState = ContentState.Idle,
        )

    fun onEmpty(country: String, language: String): NewsFeedState =
        NewsFeedState.Empty(country = country, language = language, isRefreshing = false)

    fun onNetworkError(
        state: NewsFeedState,
        error: NetworkError,
        cachedClusters: List<NewsCluster>,
        lastCachedAt: Long?,
    ): NewsFeedState {

        val newState = when {
            error is NetworkError.NoInternet && cachedClusters.isNotEmpty() ->
                NewsFeedState.Content(
                    clusters = cachedClusters,
                    lastCachedAt = lastCachedAt,
                    contentState = ContentState.Offline(lastCachedAt),
                )

            state is NewsFeedState.Content ->
                state.copy(contentState = ContentState.Idle)

            else -> NewsFeedState.Error(
                errorType = error,
                isRefreshing = false,
            )
        }

        return newState
    }
}