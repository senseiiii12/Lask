package dev.alexmester.impl.presentation.mvi

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.alexmester.impl.domain.interactor.ArticleDetailInteractor
import dev.alexmester.ui.R
import dev.alexmester.ui.uitext.UiText
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

    private var isTimeReached = false
    private var isScrollReached = false
    private var isMarkedAsRead = false

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
            ArticleDetailIntent.TimeThresholdReached -> {
                isTimeReached = true
                tryMarkAsRead()
            }
            ArticleDetailIntent.ScrollThresholdReached -> {
                isScrollReached = true
                tryMarkAsRead()
            }
        }
    }

    private fun tryMarkAsRead() {
        if (isMarkedAsRead) return
        if (!isTimeReached || !isScrollReached) return

        val article = (_state.value as? ArticleDetailState.Content)?.article ?: return
        isMarkedAsRead = true
        viewModelScope.launch {
            interactor.markAsRead(article)
        }
    }

    private fun loadArticle() {
        viewModelScope.launch {
            val article = interactor.getArticle(articleId)
            if (article == null) {
                val message = UiText.StringResource(R.string.error_article_not_found)
                _state.value = ArticleDetailState.Error(message)
            } else {
                _state.value = ArticleDetailState.Content(article = article)
            }
        }
    }

    private fun observeBookmark() {
        interactor.isBookmarked(articleId)
            .onEach { isBookmarked ->
                _state.update { ArticleDetailReducer.onBookmarkUpdate(it, isBookmarked) }
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
            val msg = if (nowBookmarked)
                UiText.StringResource(R.string.add_to_bookmark)
            else
                UiText.StringResource(R.string.removed_from_bookmark)
            emitSideEffect(ArticleDetailSideEffect.ShowSnackbar(msg))
        }
    }

    private fun emitSideEffect(effect: ArticleDetailSideEffect) {
        viewModelScope.launch { _sideEffects.send(effect) }
    }
}