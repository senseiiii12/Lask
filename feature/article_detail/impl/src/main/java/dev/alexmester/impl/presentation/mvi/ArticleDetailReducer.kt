package dev.alexmester.impl.presentation.mvi


object ArticleDetailReducer {

    fun onBookmarkUpdated(
        state: ArticleDetailState,
        isBookmarked: Boolean,
    ): ArticleDetailState =
        state.contentOrNull?.copy(isBookmarked = isBookmarked) ?: state

    fun onClapCountUpdated(
        state: ArticleDetailState,
        count: Int,
    ): ArticleDetailState =
        state.contentOrNull?.copy(clapCount = count) ?: state

    fun onClapAnimating(
        state: ArticleDetailState,
        isAnimating: Boolean,
    ): ArticleDetailState =
        state.contentOrNull?.copy(isClapAnimating = isAnimating) ?: state
}