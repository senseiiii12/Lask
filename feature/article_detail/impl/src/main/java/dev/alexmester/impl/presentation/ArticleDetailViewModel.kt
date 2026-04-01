package dev.alexmester.impl.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.alexmester.impl.domain.interactor.ArticleDetailInteractor
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ArticleDetailViewModel(
    private val interactor: ArticleDetailInteractor,
    private val articleId: Long,
    private val articleUrl: String,
) : ViewModel() {

    private val _state = MutableStateFlow<ArticleDetailState>(ArticleDetailState.Loading)
    val state: StateFlow<ArticleDetailState> = _state.asStateFlow()

    private val _sideEffects = Channel<ArticleDetailSideEffect>(Channel.BUFFERED)
    val sideEffects = _sideEffects.receiveAsFlow()

    init {
        loadArticle()
        observeBookmark()
        observeClapCount()
    }

    fun handleIntent(intent: ArticleDetailIntent) {
        _state.update { ArticleDetailReducer.reduce(it, intent) }

        when (intent) {
            ArticleDetailIntent.Back -> emitSideEffect(ArticleDetailSideEffect.NavigateBack)
            ArticleDetailIntent.Clap -> onClap()
            ArticleDetailIntent.ToggleBookmark -> onToggleBookmark()
            ArticleDetailIntent.Share -> {
                emitSideEffect(ArticleDetailSideEffect.ShareUrl(articleUrl))
            }
        }
    }

    private fun loadArticle() {
        viewModelScope.launch {
            val article = interactor.getArticle(articleId)
            if (article == null) {
                _state.value = ArticleDetailState.Error("Статья не найдена")
            } else {
                _state.value = ArticleDetailState.Content(article = article)
            }
        }
    }

    private fun observeBookmark() {
        interactor.isBookmarked(articleId)
            .onEach { isBookmarked ->
                _state.update { ArticleDetailReducer.onBookmarkSynced(it, isBookmarked) }
            }
            .launchIn(viewModelScope)
    }

    private fun observeClapCount() {
        interactor.getClapCount(articleId)
            .onEach { count ->
                _state.update { ArticleDetailReducer.onClapCountUpdated(it, count) }
            }
            .launchIn(viewModelScope)
    }

    private fun onClap() {
        viewModelScope.launch {
            interactor.addClap(articleId)
        }
    }

    private fun onToggleBookmark() {
        val content = _state.value as? ArticleDetailState.Content ?: return
        viewModelScope.launch {
            val nowBookmarked = interactor.toggleBookmark(content.article)
            val msg = if (nowBookmarked) "Добавлено в закладки" else "Удалено из закладок"
            emitSideEffect(ArticleDetailSideEffect.ShowSnackbar(msg))
        }
    }

    private fun emitSideEffect(effect: ArticleDetailSideEffect) {
        viewModelScope.launch { _sideEffects.send(effect) }
    }
}