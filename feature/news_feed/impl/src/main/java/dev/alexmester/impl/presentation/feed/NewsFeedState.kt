package dev.alexmester.newsfeed.impl.presentation.feed

import dev.alexmester.models.news.NewsCluster

//data class NewsFeedState(
//    val clusters: List<NewsCluster> = emptyList(),
//    val isLoading: Boolean = false,
//    val isRefreshing: Boolean = false,
//    val error: String? = null,
//    val isOffline: Boolean = false,
//    val lastCachedAt: Long? = null,
//)

sealed interface NewsFeedScreenState {
    data object Loading : NewsFeedScreenState
    data class Error(val message: String) : NewsFeedScreenState
    data class Content(
        val clusters: List<NewsCluster>,
        val lastCachedAt: Long? = null,
        val contentState: ContentState = ContentState.Idle,
    ) : NewsFeedScreenState
}

sealed interface ContentState {
    data object Idle : ContentState
    data object Refreshing : ContentState
    data class Offline(val lastCachedAt: Long?) : ContentState
}
