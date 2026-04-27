package dev.alexmester.impl.presentstion.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.alexmester.error.NetworkErrorUiMapper
import dev.alexmester.impl.domain.usecase.GetLastCachedAtExploreUseCase
import dev.alexmester.impl.domain.usecase.LoadMoreExploreUseCase
import dev.alexmester.impl.domain.usecase.ObserveArticlesExploreUseCase
import dev.alexmester.impl.domain.usecase.ObserveReadArticleIdsExploreUseCase
import dev.alexmester.impl.domain.usecase.RefreshExploreUseCase
import dev.alexmester.models.error.NetworkError
import dev.alexmester.models.result.onFailure
import dev.alexmester.models.result.onSuccess
import dev.alexmester.ui.R
import dev.alexmester.ui.uitext.UiText
import dev.alexmester.utils.constants.LaskConstants.PAGE_SIZE
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
    private val refreshExplore: RefreshExploreUseCase,
    private val loadMore: LoadMoreExploreUseCase,
    private val observeArticles: ObserveArticlesExploreUseCase,
    private val observeReadIds: ObserveReadArticleIdsExploreUseCase,
    private val getLastCachedAt: GetLastCachedAtExploreUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow<ExploreState>(ExploreState.Loading)
    val state: StateFlow<ExploreState> = _state.asStateFlow()

    private val _sideEffects = Channel<ExploreSideEffect>(Channel.BUFFERED)
    val sideEffects = _sideEffects.receiveAsFlow()

    val readArticleIds: StateFlow<Set<Long>> =
        observeReadIds()
            .map { it.toSet() }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                emptySet()
            )

    init {
        observeLocalCache()
        bootstrap()
    }

    fun handleIntent(intent: ExploreIntent) {
        when (intent) {
            ExploreIntent.Refresh -> refresh()
            ExploreIntent.LoadMore -> loadMore()
            is ExploreIntent.ArticleClick ->
                emitSideEffect(
                    ExploreSideEffect.NavigateToArticle(
                        intent.articleId,
                        intent.articleUrl
                    )
                )
        }
    }

    private fun observeLocalCache() {
        observeArticles()
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
            _state.value = ExploreState.Loading
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
            refreshExplore()
                .onSuccess { result ->
                    val currentState = _state.value
                    if (result == 0) {
                        if (!currentState.isContent) {
                            _state.value = ExploreState.EmptyInterests(isRefreshing = false)
                            return@launch
                        } else {
                            emitSideEffect(
                                ExploreSideEffect.ShowWarning(
                                    UiText.StringResource(R.string.interests_you_not_added)
                                )
                            )
                        }
                    }
                    _state.update { current ->
                        ExploreState.Content(
                            articles = current.contentOrNull?.articles.orEmpty(),
                            isRefreshing = false,
                            isLoadingMore = false,
                            endReached = result < PAGE_SIZE,
                            lastCachedAt = getLastCachedAt(),
                            isOffline = false,
                        )
                    }
                }
                .onFailure { error ->
                    handleError(error)
                }
        }
    }

    private fun loadMore() {
        val current = _state.value.contentOrNull ?: return
        if (current.isLoadingMore || current.isRefreshing || current.endReached) return

        _state.updateContent { it.copy(isLoadingMore = true) }

        viewModelScope.launch {
            loadMore(offset = current.articles.size)
                .onSuccess { result ->
                    _state.updateContent {
                        it.copy(
                            isLoadingMore = false,
                            endReached = result < PAGE_SIZE,
                            isOffline = false,
                        )
                    }
                }
                .onFailure { error ->
                    _state.updateContent { it.copy(isLoadingMore = false) }

                    emitSideEffect(
                        ExploreSideEffect.ShowError(
                            NetworkErrorUiMapper.toUiText(error)
                        )
                    )
                }
        }
    }

    private fun handleError(error: NetworkError) {
        val message = NetworkErrorUiMapper.toUiText(error)

        _state.update { current ->
            when {
                error is NetworkError.NoInternet &&
                current is ExploreState.Content &&
                current.articles.isNotEmpty() -> {
                    current.copy(isRefreshing = false, isOffline = true)
                }

                current is ExploreState.Content ->
                    current.copy(isRefreshing = false)

                else -> ExploreState.Error(error)
            }
        }
        emitSideEffect(ExploreSideEffect.ShowError(message))
    }

    private fun emitSideEffect(effect: ExploreSideEffect) {
        _sideEffects.trySend(effect)
    }
}