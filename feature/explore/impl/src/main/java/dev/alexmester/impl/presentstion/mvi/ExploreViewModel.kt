package dev.alexmester.impl.presentstion.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.alexmester.error.NetworkErrorUiMapper
import dev.alexmester.impl.domain.interactor.ExploreInteractor
import dev.alexmester.models.error.NetworkError
import dev.alexmester.models.result.AppResult
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

class ExploreViewModel(
    private val interactor: ExploreInteractor,
) : ViewModel() {

    private val pageSize = 20

    private val _state = MutableStateFlow<ExploreState>(ExploreState.Loading)
    val state: StateFlow<ExploreState> = _state.asStateFlow()

    private val _sideEffects = Channel<ExploreSideEffect>(Channel.BUFFERED)
    val sideEffects = _sideEffects.receiveAsFlow()

    val readArticleIds: StateFlow<Set<Long>> = interactor.observeReadArticleIds()
        .map { it.toSet() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptySet(),
        )

    init {
        observeLocalCache()
        bootstrap()
    }

    fun handleIntent(intent: ExploreIntent) {
        when (intent) {
            ExploreIntent.Refresh -> refresh()
            ExploreIntent.LoadMore -> loadMore()
            is ExploreIntent.ArticleClick -> emitSideEffect(
                ExploreSideEffect.NavigateToArticle(
                    articleId = intent.articleId,
                    articleUrl = intent.articleUrl,
                )
            )
        }
    }

    private fun observeLocalCache() {
        interactor.observeArticles()
            .onEach { articles ->
                if (articles.isEmpty()) return@onEach
                _state.update { current ->
                    when (current) {
                        is ExploreState.Content -> current.copy(articles = articles)
                        else -> ExploreState.Content(articles = articles)
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    private fun bootstrap() {
        viewModelScope.launch {
            val query = interactor.getExploreQueryOrNull()
            if (query.isNullOrBlank()) {
                _state.value = ExploreState.EmptyInterests(isRefreshing = false)
                return@launch
            }
            if (!_state.value.isContent) {
                _state.value = ExploreState.Loading
            }
            refresh()
        }
    }

    private fun refresh() {
        _state.update { current ->
            when (current) {
                is ExploreState.Content -> current.copy(isRefreshing = true, isOffline = false)
                is ExploreState.Error -> current.copy(isRefreshing = true)
                is ExploreState.EmptyInterests -> current.copy(isRefreshing = true)
                else -> current
            }
        }

        viewModelScope.launch {
            val query = interactor.getExploreQueryOrNull()
            if (query.isNullOrBlank()) {
                _state.value = ExploreState.EmptyInterests()
                return@launch
            }

            val result = interactor.refresh(pageSize = pageSize)
            when (result) {
                is AppResult.Success -> {
                    val cachedAt = interactor.getLastCachedAt()
                    _state.update { current ->
                        val articles = current.contentOrNull?.articles ?: emptyList()
                        ExploreState.Content(
                            articles = articles,
                            isRefreshing = false,
                            isLoadingMore = false,
                            endReached = result.data < pageSize,
                            lastCachedAt = cachedAt,
                            isOffline = false,
                        )
                    }
                }

                is AppResult.Failure -> handleError(result.error)
            }
        }
    }

    private fun loadMore() {
        val current = _state.value.contentOrNull ?: return
        if (current.isLoadingMore || current.isRefreshing || current.endReached) return

        _state.update { it.contentOrNull?.copy(isLoadingMore = true) ?: it }

        viewModelScope.launch {
            when (val result = interactor.loadMore(
                pageSize = pageSize,
                offset = current.articles.size,
            )) {
                is AppResult.Success -> {
                    _state.update { state ->
                        state.contentOrNull?.copy(
                            isLoadingMore = false,
                            endReached = result.data < pageSize,
                            isOffline = false,
                        ) ?: state
                    }
                }

                is AppResult.Failure -> {
                    _state.update { state ->
                        state.contentOrNull?.copy(isLoadingMore = false) ?: state
                    }
                    emitSideEffect(ExploreSideEffect.ShowError(NetworkErrorUiMapper.toUiText(result.error)))
                }
            }
        }
    }

    private fun handleError(error: NetworkError) {
        val message = NetworkErrorUiMapper.toUiText(error)
        _state.update { current ->
            when {
                error is NetworkError.NoInternet && current is ExploreState.Content
                && current.articles.isNotEmpty() -> {
                    current.copy(isRefreshing = false, isOffline = true)
                }

                current is ExploreState.Content -> {
                    current.copy(isRefreshing = false)
                }

                else -> ExploreState.Error(errorType = error)
            }
        }
        emitSideEffect(ExploreSideEffect.ShowError(message))
    }

    private fun emitSideEffect(effect: ExploreSideEffect) {
        viewModelScope.launch { _sideEffects.send(effect) }
    }
}