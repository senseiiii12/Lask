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
import dev.alexmester.impl.presentation.mvi.ArticleDetailState
import dev.alexmester.impl.presentation.mvi.IsTranslateLoading
import dev.alexmester.impl.presentation.mvi.IsTranslated
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
    contentState: ArticleDetailState.Content,
    onIntent: (ArticleDetailIntent) -> Unit,
    hazeState: HazeState,
    modifier: Modifier = Modifier,
) {
    val isTranslateEnabled = contentState.autoTranslateLanguage != contentState.article.language &&
            !contentState.IsTranslateLoading

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
            LaskClapButton(
                count = contentState.clapCount,
                onClick = { onIntent(ArticleDetailIntent.Clap) },
            )
            LaskBookmarkButton(
                isBookmarked = contentState.isBookmarked,
                onClick = { onIntent(ArticleDetailIntent.ToggleBookmark) },
                style = BookmarkButtonStyle.IconBottomBar,
            )
            LaskTranslateButton(
                isTranslated = contentState.IsTranslated,
                isEnabled = isTranslateEnabled,
                onClick = {
                    if (contentState.IsTranslated) onIntent(ArticleDetailIntent.RevertTranslation)
                    else onIntent(ArticleDetailIntent.Translate)
                },
            )
            LaskShareButton(
                onClick = { onIntent(ArticleDetailIntent.Share) }
            )
        }
    }
}