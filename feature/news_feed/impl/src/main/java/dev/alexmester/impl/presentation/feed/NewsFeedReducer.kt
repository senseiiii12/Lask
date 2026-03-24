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

    fun onClustersLoaded(
        clusters: List<NewsCluster>,
        lastCachedAt: Long?,
    ): NewsFeedScreenState = NewsFeedScreenState.Content(
        clusters = clusters,
        lastCachedAt = lastCachedAt,
        contentState = ContentState.Idle,
    )

    fun onRefreshSuccess(
        clusters: List<NewsCluster>,
        lastCachedAt: Long?,
    ): NewsFeedScreenState = NewsFeedScreenState.Content(
        clusters = clusters,
        lastCachedAt = lastCachedAt,
        contentState = ContentState.Idle,
    )

    fun onError(
        state: NewsFeedScreenState,
        message: String,
    ): NewsFeedScreenState = when (state) {
        // Данные есть — оставляем Content, убираем Refreshing
        is NewsFeedScreenState.Content -> state.copy(
            contentState = ContentState.Idle,
        )
        // Данных нет — показываем Error
        else -> NewsFeedScreenState.Error(message)
    }

    fun onOffline(
        state: NewsFeedScreenState,
        clusters: List<NewsCluster>,
        lastCachedAt: Long?,
    ): NewsFeedScreenState = when {
        clusters.isNotEmpty() -> NewsFeedScreenState.Content(
            clusters = clusters,
            lastCachedAt = lastCachedAt,
            contentState = ContentState.Offline(lastCachedAt),
        )
        else -> NewsFeedScreenState.Error("Нет подключения к сети")
    }
}