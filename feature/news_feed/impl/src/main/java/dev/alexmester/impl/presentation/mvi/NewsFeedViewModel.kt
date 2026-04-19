package dev.alexmester.impl.presentation.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.alexmester.impl.domain.usecase.GetCurrentLocaleUseCase
import dev.alexmester.impl.domain.usecase.ObserveFeedClustersUseCase
import dev.alexmester.impl.domain.usecase.GetLastCachedAtUseCase
import dev.alexmester.impl.domain.usecase.ObserveReadArticleIdsUseCase
import dev.alexmester.impl.domain.usecase.RefreshFeedUseCase
import dev.alexmester.models.news.NewsCluster
import dev.alexmester.models.result.AppResult
import dev.alexmester.newsfeed.impl.presentation.feed.NewsFeedIntent
import dev.alexmester.newsfeed.impl.presentation.feed.NewsFeedReducer
import dev.alexmester.newsfeed.impl.presentation.feed.NewsFeedState
import dev.alexmester.newsfeed.impl.presentation.feed.NewsFeedSideEffect
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

    private var lastKnownCountry: String? = null
    private var isFeedLoaded = false

    init {
        observeClusters()
    }

    fun handleIntent(intent: NewsFeedIntent) {
        _state.update { NewsFeedReducer.reduce(it, intent) }

        when (intent) {
            is NewsFeedIntent.Refresh -> loadFeed()
            is NewsFeedIntent.ArticleClick -> emitSideEffect(
                NewsFeedSideEffect.NavigateToArticle(intent.articleId, intent.articleUrl)
            )
        }
    }

    private fun observeClusters() {
        observeFeedClusters()
            .onEach { (clusters, prefs) ->
                when {
                    isCountryChanged(prefs.defaultCountry) -> onCountryChanged(prefs.defaultCountry)
                    !isFeedLoaded -> onFirstLoad(clusters, prefs.defaultCountry)
                    else -> onCacheUpdated(clusters, prefs.defaultCountry)
                }
            }
            .launchIn(viewModelScope)
    }



// ── Handlers ──────────────────────────────────────────────────────────────────

    private fun isCountryChanged(newCountry: String): Boolean =
        lastKnownCountry != null && lastKnownCountry != newCountry

    private fun onCountryChanged(newCountry: String) {
        lastKnownCountry = newCountry
        isFeedLoaded = false
        _state.update { NewsFeedState.Loading }
        loadFeed()
    }

    private suspend fun onFirstLoad(clusters: List<NewsCluster>, country: String) {
        lastKnownCountry = country
        isFeedLoaded = true
        showCacheIfAvailable(clusters, country)
        loadFeed()
    }

    private suspend fun onCacheUpdated(clusters: List<NewsCluster>, country: String) {
        if (_state.value.isOffline) return
        showCacheIfAvailable(clusters, country)
    }

// ── State helpers ─────────────────────────────────────────────────────────────

    private suspend fun showCacheIfAvailable(clusters: List<NewsCluster>, country: String) {
        if (clusters.isEmpty()) return
        _state.update {
            NewsFeedReducer.onClustersLoaded(
                clusters = clusters,
                lastCachedAt = getLastCachedAt(),
                country = country,
            )
        }
    }

// ── Network ───────────────────────────────────────────────────────────────────

    private fun loadFeed() {
        viewModelScope.launch {
            handleNetworkResult(refreshFeed())
        }
    }

    private fun handleNetworkResult(result: AppResult<Int>) {
        when (result) {
            is AppResult.Success -> {
                if (result.data == 0) {
                    viewModelScope.launch {
                        val (country, language) = getCurrentLocale()
                        val clusters = _state.value.contentOrNull?.clusters ?: emptyList()
                        if (clusters.isEmpty()) {
                            _state.update { NewsFeedReducer.onEmpty(country, language) }
                        }
                    }
                }
            }
            is AppResult.Failure -> {
                val currentState = _state.value
                val (newState, message) = NewsFeedReducer.onNetworkError(
                    state = currentState,
                    error = result.error,
                    cachedClusters = currentState.contentOrNull?.clusters ?: emptyList(),
                    lastCachedAt = currentState.contentOrNull?.lastCachedAt,
                )
                _state.update { newState }
                emitSideEffect(NewsFeedSideEffect.ShowError(message))
            }
        }
    }

    private fun emitSideEffect(effect: NewsFeedSideEffect) {
        viewModelScope.launch { _sideEffects.send(effect) }
    }
}