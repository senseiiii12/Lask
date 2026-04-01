package dev.alexmester.impl.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.KeyboardBackspace
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.alexmester.impl.presentation.ArticleDetailIntent
import dev.alexmester.impl.presentation.ArticleDetailState
import dev.alexmester.ui.desing_system.LaskColors
import dev.alexmester.ui.desing_system.LaskPalette
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.rememberHazeState

@Composable
fun ArticleDetailBottomBar(
    state: ArticleDetailState.Content,
    onIntent: (ArticleDetailIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val hazeState = rememberHazeState()

    val bookmarkColor by animateColorAsState(
        targetValue = if (state.isBookmarked) LaskPalette.Bookmark
        else MaterialTheme.LaskColors.textPrimary,
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.LaskColors.backgroundPrimary)
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

        IconButton(onClick = { onIntent(ArticleDetailIntent.Back) }) {
            Icon(
                imageVector = Icons.Default.KeyboardBackspace,
                contentDescription = null,
                tint = MaterialTheme.LaskColors.textPrimary,
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ){
            ClapButton(
                count = state.clapCount,
                isAnimating = state.isClapAnimating,
                onClick = { onIntent(ArticleDetailIntent.Clap) },
            )
            IconButton(onClick = { onIntent(ArticleDetailIntent.ToggleBookmark) }) {
                Icon(
                    imageVector = if (state.isBookmarked) Icons.Filled.Bookmark
                    else Icons.Outlined.BookmarkBorder,
                    contentDescription = null,
                    tint = bookmarkColor,
                )
            }
            IconButton(onClick = { onIntent(ArticleDetailIntent.Share) }) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = null,
                    tint = MaterialTheme.LaskColors.textPrimary,
                )
            }
        }
    }
}