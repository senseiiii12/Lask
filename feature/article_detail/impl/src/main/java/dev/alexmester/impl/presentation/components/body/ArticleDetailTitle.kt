package dev.alexmester.impl.presentation.components.body

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.alexmester.impl.presentation.mvi.TranslationState
import dev.alexmester.ui.components.shimmer.ShimmerTextBlock
import dev.alexmester.ui.desing_system.LaskColors
import dev.alexmester.ui.desing_system.LaskTypography
import dev.alexmester.ui.transition.sharedElementIfAvailable

@Composable
internal fun ArticleDetailTitle(
    title: String,
    translatedTitle: String?,
    translationState: TranslationState,
    articleId: Long,
) {
    val displayTitle = when {
        translationState is TranslationState.Translated && translatedTitle != null -> translatedTitle
        else -> title
    }

    AnimatedContent(
        targetState = translationState is TranslationState.Loading,
        label = "titleShimmer",
    ) { isLoading ->
        if (isLoading) {
            ShimmerTextBlock(
                modifier = Modifier.fillMaxWidth(),
                lineCount = 2,
                lineHeight = 22.dp ,
            )
        } else {
            Text(
                text = displayTitle,
                style = MaterialTheme.LaskTypography.h4,
                color = MaterialTheme.LaskColors.textPrimary,
                modifier = Modifier.sharedElementIfAvailable(key = "title_$articleId"),
            )
        }
    }
}