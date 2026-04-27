package dev.alexmester.impl.presentstion.mvi

import dev.alexmester.models.error.NetworkError
import dev.alexmester.models.news.NewsArticle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

sealed interface ExploreState {
    data object Loading : ExploreState

    data class Error(
        val errorType: NetworkError,
        val isRefreshing: Boolean = false
    ) : ExploreState

    data class EmptyInterests(
        val isRefreshing: Boolean = false
    ) : ExploreState

    data class Content(
        val articles: List<NewsArticle>,
        val isRefreshing: Boolean = false,
        val isLoadingMore: Boolean = false,
        val endReached: Boolean = false,
        val lastCachedAt: Long? = null,
        val isOffline: Boolean = false,
    ) : ExploreState
}

val ExploreState.contentOrNull: ExploreState.Content?
    get() = this as? ExploreState.Content

val ExploreState.isContent: Boolean
    get() = this is ExploreState.Content

inline fun MutableStateFlow<ExploreState>.updateContent(
    block: (ExploreState.Content) -> ExploreState.Content
) {
    update { state ->
        if (state is ExploreState.Content) block(state) else state
    }
}