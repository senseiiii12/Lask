package dev.alexmester.newsfeed.impl.presentation.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.alexmester.impl.domain.interactor.NewsFeedInteractor
import dev.alexmester.models.error.NetworkError
import dev.alexmester.models.result.AppResult
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch



class NewsFeedViewModel(
    private val interactor: NewsFeedInteractor,
) : ViewModel() {

    private val _state = MutableStateFlow<NewsFeedScreenState>(NewsFeedScreenState.Loading)
    val state: StateFlow<NewsFeedScreenState> = _state.asStateFlow()

    private val _sideEffects = Channel<NewsFeedSideEffect>(Channel.BUFFERED)
    val sideEffects = _sideEffects.receiveAsFlow()

    init {
        observeClusters()
        loadFeed()
    }

    fun handleIntent(intent: NewsFeedIntent) {
        _state.update { NewsFeedReducer.reduce(it, intent) }

        when (intent) {
            is NewsFeedIntent.Refresh -> refresh()
            is NewsFeedIntent.ArticleClick -> navigateToArticle(intent)
        }
    }

    /**
     * Подписка на Room Flow — каждый раз когда данные меняются в базе
     * обновляем Content state. Loading → Content происходит здесь.
     */
    private fun observeClusters() {
        interactor.getClustersFlow()
            .onEach { clusters ->
                val lastCachedAt = interactor.getLastCachedAt()
                // Не перезаписываем Offline state — баннер должен остаться
                val currentState = _state.value
                if (currentState !is NewsFeedScreenState.Content ||
                    (currentState as? NewsFeedScreenState.Content)?.contentState !is ContentState.Offline
                ) {
                    _state.update {
                        NewsFeedReducer.onClustersLoaded(clusters, lastCachedAt)
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    private fun loadFeed() {
        viewModelScope.launch {
            when (val result = interactor.refresh(forceRefresh = false)) {
                is AppResult.Success -> Unit // observeClusters обновит state
                is AppResult.Failure -> handleError(result.error)
            }
        }
    }

    private fun refresh() {
        viewModelScope.launch {
            when (val result = interactor.refresh(forceRefresh = true)) {
                is AppResult.Success -> Unit // observeClusters обновит state
                is AppResult.Failure -> handleError(result.error)
            }
        }
    }

    private fun navigateToArticle(intent: NewsFeedIntent.ArticleClick) {
        viewModelScope.launch {
            _sideEffects.send(
                NewsFeedSideEffect.NavigateToArticle(
                    articleId = intent.articleId,
                    articleUrl = intent.articleUrl,
                )
            )
        }
    }

    private fun handleError(error: NetworkError) {
        viewModelScope.launch {
            val currentState = _state.value
            when (error) {
                is NetworkError.NoInternet -> {
                    val clusters = (currentState as? NewsFeedScreenState.Content)?.clusters ?: emptyList()
                    val lastCachedAt = interactor.getLastCachedAt()
                    _state.update {
                        NewsFeedReducer.onOffline(
                            state = currentState,
                            clusters = clusters,
                            lastCachedAt = lastCachedAt,
                        )
                    }
                }
                is NetworkError.RateLimit -> {
                    val message = "Превышен лимит запросов. Попробуйте позже"
                    _state.update { NewsFeedReducer.onError(currentState, message) }
                    _sideEffects.send(NewsFeedSideEffect.ShowError(message))
                }
                else -> {
                    val message = "Ошибка загрузки новостей"
                    _state.update { NewsFeedReducer.onError(currentState, message) }
                    _sideEffects.send(NewsFeedSideEffect.ShowError(message))
                }
            }
        }
    }
}
