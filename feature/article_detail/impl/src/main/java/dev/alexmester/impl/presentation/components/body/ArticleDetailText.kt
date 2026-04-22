package dev.alexmester.impl.presentation.components.body

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.alexmester.impl.presentation.mvi.TranslationState
import dev.alexmester.ui.components.shimmer.ShimmerTextBlock
import dev.alexmester.ui.desing_system.LaskColors
import dev.alexmester.ui.desing_system.LaskTypography

@Composable
internal fun ArticleDetailText(
    text: String?,
    summary: String?,
    translatedText: String?,
    translationState: TranslationState,
) {
    AnimatedContent(
        targetState = translationState,
        label = "translationContent",
    ) { state ->
        when (state) {
            is TranslationState.Loading -> {
                ShimmerTextBlock(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    lineCount = 8,
                )
            }
            is TranslationState.Translated -> {
                val displayText = translatedText ?: text ?: summary ?: "Текст статьи недоступен"
                Text(
                    text = displayText,
                    style = MaterialTheme.LaskTypography.body1,
                    color = MaterialTheme.LaskColors.textPrimary,
                )
            }
            else -> {
                val displayText = text ?: summary ?: "Текст статьи недоступен"
                Text(
                    text = displayText,
                    style = MaterialTheme.LaskTypography.body1,
                    color = MaterialTheme.LaskColors.textPrimary,
                )
            }
        }
    }
}