package dev.alexmester.impl.presentation.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.alexmester.impl.domain.usecase.AddClapUseCase
import dev.alexmester.impl.domain.usecase.GetArticleUseCase
import dev.alexmester.impl.domain.usecase.GetAutoTranslateLanguageUseCase
import dev.alexmester.impl.domain.usecase.MarkAsReadUseCase
import dev.alexmester.impl.domain.usecase.ObserveClapCountUseCase
import dev.alexmester.impl.domain.usecase.ObserveIsBookmarkedUseCase
import dev.alexmester.impl.domain.usecase.ToggleBookmarkUseCase
import dev.alexmester.impl.domain.usecase.TranslateTextsUseCase
import dev.alexmester.models.result.onFailure
import dev.alexmester.models.result.onSuccess
import dev.alexmester.ui.R
import dev.alexmester.ui.uitext.UiText
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ArticleDetailViewModel(
    private val articleId: Long,
    private val articleUrl: String,
    private val getArticleUseCase: GetArticleUseCase,
    private val observeIsBookmarkedUseCase: ObserveIsBookmarkedUseCase,
    private val observeClapCountUseCase: ObserveClapCountUseCase,
    private val toggleBookmarkUseCase: ToggleBookmarkUseCase,
    private val addClapUseCase: AddClapUseCase,
    private val markAsReadUseCase: MarkAsReadUseCase,
    private val translateTextsUseCase: TranslateTextsUseCase,
    private val getAutoTranslateLanguageUseCase: GetAutoTranslateLanguageUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow<ArticleDetailState>(ArticleDetailState.Loading)
    val state: StateFlow<ArticleDetailState> = _state.asStateFlow()

    private val _sideEffects = Channel<ArticleDetailSideEffect>(Channel.BUFFERED)
    val sideEffects = _sideEffects.receiveAsFlow()

    private var isTimeThresholdReached = false
    private var isScrollThresholdReached = false
    private var isMarkedAsRead = false

    init {
        loadArticle()
    }

    fun handleIntent(intent: ArticleDetailIntent) {
        when (intent) {
            is ArticleDetailIntent.Back ->
                emitSideEffect(ArticleDetailSideEffect.NavigateBack)

            is ArticleDetailIntent.Clap -> onClap()

            is ArticleDetailIntent.ToggleBookmark -> onToggleBookmark()

            is ArticleDetailIntent.Share ->
                emitSideEffect(ArticleDetailSideEffect.ShareUrl(articleUrl))

            is ArticleDetailIntent.TimeThresholdReached -> {
                isTimeThresholdReached = true
                tryMarkAsRead()
            }

            is ArticleDetailIntent.ScrollThresholdReached -> {
                isScrollThresholdReached = true
                tryMarkAsRead()
            }

            is ArticleDetailIntent.Translate -> onTranslate()

            is ArticleDetailIntent.RevertTranslation -> onRevertTranslation()
        }
    }

    private fun loadArticle() {
        viewModelScope.launch {
            val article = getArticleUseCase(articleId) ?: run {
                _state.value = ArticleDetailState.Error(
                    UiText.StringResource(R.string.error_article_not_found)
                )
                return@launch
            }
            combine(
                observeIsBookmarkedUseCase(articleId),
                observeClapCountUseCase(articleId)
            ) { isBookmarked, clapCount ->
                ArticleDetailState.Content(
                    article = article,
                    isBookmarked = isBookmarked,
                    clapCount = clapCount,
                    autoTranslateLanguage = getAutoTranslateLanguageUseCase(),
                )
            }.collect {
                _state.value = it
            }
        }
    }

    private fun onTranslate() {
        val content = _state.value.contentOrNull ?: return
        val targetLang = content.autoTranslateLanguage

        _state.update { it.contentOrNull?.copy(translationState = TranslationState.Loading) ?: it }

        viewModelScope.launch {
            val article = content.article
            val bodyText = article.text ?: article.summary ?: ""
            val sourceLang = article.language

            translateTextsUseCase(
                title = article.title,
                bodyText = bodyText,
                targetLanguage = targetLang,
                sourceLanguage = sourceLang,
            )
            .onSuccess { (translatedTitle, translatedBody) ->
                _state.update {
                    it.contentOrNull?.copy(
                        translationState = TranslationState.Translated,
                        translatedTitle = translatedTitle,
                        translatedText = translatedBody,
                    ) ?: it
                }
            }
            .onFailure { errorType ->
                _state.update {
                    it.contentOrNull?.copy(
                        translationState = TranslationState.Idle
                    ) ?: it
                }
                emitSideEffect(ArticleDetailSideEffect.ShowError(errorType = errorType))
            }
        }
    }

    private fun onRevertTranslation() {
        _state.update {
            it.contentOrNull?.copy(
                translationState = TranslationState.Idle,
                translatedTitle = null,
                translatedText = null,
            ) ?: it
        }
    }

    private fun onClap() {
        viewModelScope.launch {
            addClapUseCase(articleId)
        }
    }

    private fun onToggleBookmark() {
        viewModelScope.launch {
            val nowBookmarked = toggleBookmarkUseCase(articleId)
            emitSideEffect(ArticleDetailSideEffect.ShawBookmarkActionMessage(nowBookmarked))
        }
    }

    private fun tryMarkAsRead() {
        if (isMarkedAsRead) return
        if (!isTimeThresholdReached || !isScrollThresholdReached) return
        isMarkedAsRead = true
        viewModelScope.launch {
            markAsReadUseCase(articleId)
        }
    }

    private fun emitSideEffect(effect: ArticleDetailSideEffect) {
        viewModelScope.launch { _sideEffects.send(effect) }
    }
}