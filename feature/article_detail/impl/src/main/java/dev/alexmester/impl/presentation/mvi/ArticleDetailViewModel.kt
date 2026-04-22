package dev.alexmester.impl.presentation.mvi

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
import kotlin.coroutines.cancellation.CancellationException

class ArticleDetailViewModel(
    private val interactor: ArticleDetailInteractor,
    private val articleId: Long,
    private val articleUrl: String,
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
            ArticleDetailIntent.Back ->
                emitSideEffect(ArticleDetailSideEffect.NavigateBack)

            ArticleDetailIntent.Clap -> onClap()

            ArticleDetailIntent.ToggleBookmark -> onToggleBookmark()

            ArticleDetailIntent.Share ->
                emitSideEffect(ArticleDetailSideEffect.ShareUrl(articleUrl))

            ArticleDetailIntent.TimeThresholdReached -> {
                isTimeThresholdReached = true
                tryMarkAsRead()
            }

            ArticleDetailIntent.ScrollThresholdReached -> {
                isScrollThresholdReached = true
                tryMarkAsRead()
            }
            ArticleDetailIntent.Translate -> onTranslate()

            ArticleDetailIntent.RevertTranslation -> onRevertTranslation()
        }
    }

    // ── Private ───────────────────────────────────────────────────────────────

    private fun loadArticle() {
        viewModelScope.launch {
            val article = interactor.getArticle(articleId)

            if (article == null) {
                _state.value = ArticleDetailState.Error(
                    UiText.StringResource(R.string.error_article_not_found)
                )
                return@launch
            }

            val isBookmarked = interactor.isBookmarked(articleId)
            val clapCount = interactor.getClapCount(articleId)
            val autoTranslateLang = interactor.getAutoTranslateLanguage()

            _state.value = ArticleDetailState.Content(
                article = article,
                isBookmarked = isBookmarked,
                clapCount = clapCount,
                autoTranslateLanguage = autoTranslateLang,
            )

            observeBookmark()
            observeClapCount()
        }
    }

    private fun onTranslate() {
        val content = _state.value.contentOrNull ?: return
        val targetLang = content.autoTranslateLanguage ?: return

        _state.update { it.contentOrNull?.copy(translationState = TranslationState.Loading) ?: it }

        viewModelScope.launch {
            val article = content.article
            val bodyText = article.text ?: article.summary ?: ""
            val sourceLang = article.language

            try {
                val (translatedTitle, translatedBody) = interactor.translateTexts(
                    title = article.title,
                    bodyText = bodyText,
                    targetLanguage = targetLang,
                    sourceLanguage = sourceLang,
                )
                _state.update {
                    it.contentOrNull?.copy(
                        translationState = TranslationState.Translated,
                        translatedTitle = translatedTitle,
                        translatedText = translatedBody,
                    ) ?: it
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.update {
                    it.contentOrNull?.copy(
                        translationState = TranslationState.Error(
                            UiText.StringResource(R.string.error_translation_failed)
                        )
                    ) ?: it
                }
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

    private fun observeBookmark() {
        interactor.observeIsBookmarked(articleId)
            .onEach { isBookmarked ->
                _state.update { state ->
                    state.contentOrNull?.copy(isBookmarked = isBookmarked) ?: state
                }
            }
            .launchIn(viewModelScope)
    }

    private fun observeClapCount() {
        interactor.observeClapCount(articleId)
            .onEach { count ->
                _state.update { state ->
                    state.contentOrNull?.copy(clapCount = count) ?: state
                }
            }
            .launchIn(viewModelScope)
    }

    private fun onClap() {
        viewModelScope.launch {
            interactor.addClap(articleId)
            _state.update { it.contentOrNull?.copy(isClapAnimating = true) ?: it }
        }
    }

    private fun onToggleBookmark() {
        viewModelScope.launch {
            val nowBookmarked = interactor.toggleBookmark(articleId)
            emitSideEffect(ArticleDetailSideEffect.ShowSnackbar(nowBookmarked))
        }
    }

    private fun tryMarkAsRead() {
        if (isMarkedAsRead) return
        if (!isTimeThresholdReached || !isScrollThresholdReached) return
        isMarkedAsRead = true
        viewModelScope.launch {
            interactor.markAsRead(articleId)
        }
    }

    private fun emitSideEffect(effect: ArticleDetailSideEffect) {
        viewModelScope.launch { _sideEffects.send(effect) }
    }
}