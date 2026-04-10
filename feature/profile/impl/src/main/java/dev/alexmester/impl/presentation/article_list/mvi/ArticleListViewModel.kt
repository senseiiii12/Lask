package dev.alexmester.impl.presentation.article_list.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.alexmester.api.navigation.ArticleListType
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ArticleListViewModel(
    private val type: ArticleListType,
//    private val interactor: ArticleListInteractor,
) : ViewModel() {

    private val _state = MutableStateFlow(ArticleListState())
    val state: StateFlow<ArticleListState> = _state.asStateFlow()

    private val _sideEffects = Channel<ArticleListSideEffect>(Channel.BUFFERED)
    val sideEffects = _sideEffects.receiveAsFlow()

    init {
//        observeArticles()
    }

    fun handleIntent(intent: ArticleListIntent) {
        when (intent) {
            is ArticleListIntent.SelectCategory ->
                _state.update { it.copy(selectedCategory = intent.category) }

            is ArticleListIntent.Back ->
                emitSideEffect(ArticleListSideEffect.NavigateBack)

            is ArticleListIntent.ArticleClick ->
                emitSideEffect(
                    ArticleListSideEffect.NavigateToArticle(intent.articleId, intent.articleUrl)
                )
        }
    }

//    private fun observeArticles() {
//        val flow = when (type) {
//            ArticleListType.READ -> interactor.getReadArticles()
//            ArticleListType.CLAPPED -> interactor.getClappedArticles()
//        }
//
//        flow
//            .onEach { articles ->
//                _state.update { it.copy(allArticles = articles, isLoading = false) }
//            }
//            .launchIn(viewModelScope)
//    }

    private fun emitSideEffect(effect: ArticleListSideEffect) {
        viewModelScope.launch { _sideEffects.send(effect) }
    }
}