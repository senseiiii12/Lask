package dev.alexmester.impl.presentation.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.alexmester.impl.domain.interactor.BookmarksInteractor
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BookmarksViewModel(
    private val interactor: BookmarksInteractor,
) : ViewModel() {

    private val _state = MutableStateFlow<BookmarksState>(BookmarksState.Loading)
    val state: StateFlow<BookmarksState> = _state.asStateFlow()

    private val _sideEffects = Channel<BookmarksSideEffect>(Channel.BUFFERED)
    val sideEffects = _sideEffects.receiveAsFlow()

    init {
        observeBookmarks()
    }

    fun handleIntent(intent: BookmarksIntent) {
        when (intent) {
            is BookmarksIntent.ConfirmDeletion -> confirmDeletion()
            is BookmarksIntent.CancelDeletion -> cancelDeletion()
            is BookmarksIntent.ArticleClick -> emitSideEffect(
                BookmarksSideEffect.NavigateToArticle(intent.articleId, intent.articleUrl)
            )
            else -> _state.update { BookmarksReducer.reduce(it, intent) }
        }
    }

    private fun observeBookmarks() {
        interactor.observeBookmarks()
            .onEach { articles ->
                _state.update { current ->
                    BookmarksReducer.onArticlesUpdated(current, articles)
                }
            }
            .launchIn(viewModelScope)
    }

    private fun confirmDeletion() {
        val content = _state.value.contentOrNull ?: return
        val idsToRemove = content.pendingRemovalIds

        if (idsToRemove.isEmpty()) {
            _state.update { BookmarksReducer.reduce(it, BookmarksIntent.ToggleEditMode) }
            return
        }

        viewModelScope.launch {
            _state.update { BookmarksReducer.onCloseEditMode(it) }
            interactor.removeBookmarks(idsToRemove)
        }
    }

    private fun cancelDeletion() {
        _state.update { BookmarksReducer.onCloseEditMode(it) }
    }

    private fun emitSideEffect(effect: BookmarksSideEffect) {
        viewModelScope.launch { _sideEffects.send(effect) }
    }
}