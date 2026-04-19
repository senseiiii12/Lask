package dev.alexmester.impl.presentation.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.alexmester.impl.domain.usecase.GetCurrentLocaleUseCase
import dev.alexmester.impl.domain.usecase.GetLastCachedAtUseCase
import dev.alexmester.impl.domain.usecase.ObserveFeedClustersUseCase
import dev.alexmester.impl.domain.usecase.ObserveReadArticleIdsUseCase
import dev.alexmester.impl.domain.usecase.RefreshFeedUseCase
import dev.alexmester.models.news.NewsCluster
import dev.alexmester.models.result.AppResult
import dev.alexmester.newsfeed.impl.presentation.feed.NewsFeedIntent
import dev.alexmester.newsfeed.impl.presentation.feed.NewsFeedReducer
import dev.alexmester.newsfeed.impl.presentation.feed.NewsFeedSideEffect
import dev.alexmester.newsfeed.impl.presentation.feed.NewsFeedState
import dev.alexmester.newsfeed.impl.presentation.feed.contentOrNull
import dev.alexmester.newsfeed.impl.presentation.feed.isOffline
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NewsFeedViewModel(
    private val observeFeedClusters: ObserveFeedClustersUseCase,
    private val refreshFeed: RefreshFeedUseCase,
    private val observeReadArticleIds: ObserveReadArticleIdsUseCase,
    private val getCurrentLocale: GetCurrentLocaleUseCase,
    private val getLastCachedAt: GetLastCachedAtUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow<NewsFeedState>(NewsFeedState.Loading)
    val state: StateFlow<NewsFeedState> = _state.asStateFlow()

    private val _sideEffects = Channel<NewsFeedSideEffect>(Channel.BUFFERED)
    val sideEffects = _sideEffects.receiveAsFlow()

    val readArticleIds: StateFlow<Set<Long>> = observeReadArticleIds()
        .map { it.toSet() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptySet(),
        )

    private var selectedCountry: String? = null
    private var isInitialLoadHandled = false

    init {
        observeClusters()
    }

    fun handleIntent(intent: NewsFeedIntent) {
        _state.update { NewsFeedReducer.reduce(it, intent) }

        when (intent) {
            is NewsFeedIntent.Refresh -> requestFeedRefresh()
            is NewsFeedIntent.ArticleClick -> emitSideEffect(
                NewsFeedSideEffect.NavigateToArticle(intent.articleId, intent.articleUrl)
            )
        }
    }

    private fun observeClusters() {
        observeFeedClusters()
            .onEach { (clusters, prefs) ->
                processClusterUpdate(
                    clusters = clusters,
                    country = prefs.defaultCountry,
                )
            }
            .launchIn(viewModelScope)
    }

    private suspend fun processClusterUpdate(clusters: List<NewsCluster>, country: String) {
        when {
            hasCountryChanged(country) -> handleCountryChanged(country)
            !isInitialLoadHandled -> handleInitialLoad(clusters, country)
            else -> handleCacheUpdate(clusters, country)
        }
    }

    private fun hasCountryChanged(newCountry: String): Boolean =
        selectedCountry != null && selectedCountry != newCountry

    private fun handleCountryChanged(newCountry: String) {
        selectedCountry = newCountry
        isInitialLoadHandled = false
        _state.update { NewsFeedState.Loading }
        requestFeedRefresh()
    }

    private suspend fun handleInitialLoad(clusters: List<NewsCluster>, country: String) {
        selectedCountry = country
        isInitialLoadHandled = true

        showCachedClustersIfPresent(clusters, country)
        requestFeedRefresh()
    }

    private suspend fun handleCacheUpdate(clusters: List<NewsCluster>, country: String) {
        if (_state.value.isOffline) return
        showCachedClustersIfPresent(clusters, country)
    }

    private suspend fun showCachedClustersIfPresent(clusters: List<NewsCluster>, country: String) {
        if (clusters.isEmpty()) return

        _state.update {
            NewsFeedReducer.onClustersLoaded(
                clusters = clusters,
                lastCachedAt = getLastCachedAt(),
                country = country,
            )
        }
    }

    private fun requestFeedRefresh() {
        viewModelScope.launch {
            handleRefreshResult(refreshFeed())
        }
    }

    private suspend fun handleRefreshResult(result: AppResult<Int>) {
        when (result) {
            is AppResult.Success -> handleRefreshSuccess(result.data)
            is AppResult.Failure -> handleRefreshFailure(result)
        }
    }

    private suspend fun handleRefreshSuccess(updatedItemsCount: Int) {
        if (updatedItemsCount != 0) return

        val (country, language) = getCurrentLocale()
        val currentClusters = _state.value.contentOrNull?.clusters.orEmpty()

        if (currentClusters.isEmpty()) {
            _state.update { NewsFeedReducer.onEmpty(country, language) }
        }
    }

    private fun handleRefreshFailure(result: AppResult.Failure<Int>) {
        val currentState = _state.value
        val (newState, message) = NewsFeedReducer.onNetworkError(
            state = currentState,
            error = result.error,
            cachedClusters = currentState.contentOrNull?.clusters.orEmpty(),
            lastCachedAt = currentState.contentOrNull?.lastCachedAt,
        )
        _state.update { newState }
        emitSideEffect(NewsFeedSideEffect.ShowError(message))
    }

    private fun emitSideEffect(effect: NewsFeedSideEffect) {
        viewModelScope.launch { _sideEffects.send(effect) }
    }
}
