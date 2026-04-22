package dev.alexmester.impl.presentation.components.bottom_bar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import dev.alexmester.impl.presentation.mvi.ArticleDetailIntent
import dev.alexmester.impl.presentation.mvi.TranslationState
import dev.alexmester.ui.components.buttons.BookmarkButtonStyle
import dev.alexmester.ui.components.buttons.LaskBackButton
import dev.alexmester.ui.components.buttons.LaskBookmarkButton
import dev.alexmester.ui.components.buttons.LaskClapButton
import dev.alexmester.ui.components.buttons.LaskShareButton
import dev.alexmester.ui.components.buttons.LaskTranslateButton
import dev.alexmester.ui.desing_system.LaskColors
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect

@Composable
internal fun ArticleDetailBottomBar(
    isBookmarked: Boolean,
    clapCount: Int,
    isClapAnimating: Boolean,
    translationState: TranslationState,
    autoTranslateLanguage: String?,
    articleLanguage: String?,
    onIntent: (ArticleDetailIntent) -> Unit,
    hazeState: HazeState,
    modifier: Modifier = Modifier,
) {
    val isTranslateEnabled = autoTranslateLanguage != null &&
            autoTranslateLanguage != articleLanguage &&
            translationState !is TranslationState.Loading

    val isTranslated = translationState is TranslationState.Translated

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .background(MaterialTheme.LaskColors.brand_blue10)
            .navigationBarsPadding()
            .hazeEffect(state = hazeState) {
                alpha = 0.5f
                blurRadius = 20.dp
                noiseFactor = 0.05f
            }
            .padding(horizontal = 32.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        LaskBackButton(
            onClick = { onIntent(ArticleDetailIntent.Back) }
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LaskTranslateButton(
                isTranslated = isTranslated,
                isEnabled = isTranslateEnabled,
                onClick = {
                    if (isTranslated) onIntent(ArticleDetailIntent.RevertTranslation)
                    else onIntent(ArticleDetailIntent.Translate)
                },
            )
            LaskClapButton(
                count = clapCount,
                isAnimating = isClapAnimating,
                onClick = { onIntent(ArticleDetailIntent.Clap) },
            )
            LaskBookmarkButton(
                isBookmarked = isBookmarked,
                onClick = { onIntent(ArticleDetailIntent.ToggleBookmark) },
                style = BookmarkButtonStyle.IconBottomBar,
            )
            LaskShareButton(
                onClick = { onIntent(ArticleDetailIntent.Share) }
            )
        }
    }
}