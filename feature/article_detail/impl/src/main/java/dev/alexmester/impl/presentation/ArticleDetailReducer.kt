package dev.alexmester.impl.presentation

object ArticleDetailReducer {

    fun reduce(
        state: ArticleDetailState,
        intent: ArticleDetailIntent,
    ): ArticleDetailState {
        val content = state as? ArticleDetailState.Content ?: return state
        return when (intent) {
            is ArticleDetailIntent.Clap -> content.copy(
                clapCount = content.clapCount + 1,
                isClapAnimating = true,
            )
            is ArticleDetailIntent.ToggleBookmark -> content.copy(
                isBookmarked = !content.isBookmarked,
            )
            else -> state
        }
    }

    fun onClapAnimationDone(state: ArticleDetailState): ArticleDetailState =
        (state as? ArticleDetailState.Content)?.copy(isClapAnimating = false) ?: state

    fun onBookmarkSynced(
        state: ArticleDetailState,
        isBookmarked: Boolean,
    ): ArticleDetailState =
        (state as? ArticleDetailState.Content)?.copy(isBookmarked = isBookmarked) ?: state

    fun onClapCountUpdated(
        state: ArticleDetailState,
        count: Int,
    ): ArticleDetailState =
        (state as? ArticleDetailState.Content)?.copy(clapCount = count) ?: state
}