package dev.alexmester.impl.presentation.mvi

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.alexmester.impl.domain.interactor.NewsFeedInteractor
import dev.alexmester.models.news.NewsCluster
import dev.alexmester.models.result.AppResult
import dev.alexmester.newsfeed.impl.presentation.feed.NewsFeedIntent
import dev.alexmester.newsfeed.impl.presentation.feed.NewsFeedReducer
import dev.alexmester.newsfeed.impl.presentation.feed.NewsFeedScreenState
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
    private val interactor: NewsFeedInteractor,
) : ViewModel() {

    private val _state = MutableStateFlow<NewsFeedScreenState>(NewsFeedScreenState.Loading)
    val state: StateFlow<NewsFeedScreenState> = _state.asStateFlow()

    private val _sideEffects = Channel<NewsFeedSideEffect>(Channel.Factory.BUFFERED)
    val sideEffects = _sideEffects.receiveAsFlow()

    val readArticleIds: StateFlow<Set<Long>> = interactor
        .getReadArticleIdsFlow()
        .map { it.toSet() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptySet(),
        )

    private var lastKnownCountry: String? = null
    private var isFeedLoaded = false

    init {
        observeClustersWithPrefs()
    }

    fun handleIntent(intent: NewsFeedIntent) {
        _state.update { NewsFeedReducer.reduce(it, intent) }

        when (intent) {
            is NewsFeedIntent.Refresh -> refresh()
            is NewsFeedIntent.ArticleClick -> emitSideEffect(
                NewsFeedSideEffect.NavigateToArticle(intent.articleId, intent.articleUrl)
            )
        }
    }
    private fun refresh() {
        viewModelScope.launch {
            handleNetworkResult(interactor.refresh())
        }
    }
    private fun observeClustersWithPrefs() {
        interactor.getClustersWithPrefsFlow()
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
        _state.update { NewsFeedScreenState.Loading }
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
        val lastCachedAt = interactor.getLastCachedAt()
        _state.update {
            NewsFeedReducer.onClustersLoaded(
                clusters = clusters,
                lastCachedAt = lastCachedAt,
                country = country,
            )
        }
    }

// ── Network ───────────────────────────────────────────────────────────────────

    private fun loadFeed() {
        viewModelScope.launch {
            handleNetworkResult(interactor.refresh())
        }
    }

    private fun handleNetworkResult(result: AppResult<Unit>) {
        if (result !is AppResult.Failure) return

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

    private fun emitSideEffect(effect: NewsFeedSideEffect) {
        viewModelScope.launch { _sideEffects.send(effect) }
    }
}