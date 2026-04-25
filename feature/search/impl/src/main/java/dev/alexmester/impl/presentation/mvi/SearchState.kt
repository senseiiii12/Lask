package dev.alexmester.impl.presentation.mvi

import dev.alexmester.impl.domain.model.SearchFilters
import dev.alexmester.models.error.NetworkError
import dev.alexmester.models.news.NewsArticle
import dev.alexmester.utils.common.UiText

data class SearchState(
    val query: String = "",
    val filters: SearchFilters = SearchFilters(),
    val results: List<NewsArticle> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val endReached: Boolean = false,
    val error: NetworkError? = null,
    val hasSearched: Boolean = false,
)