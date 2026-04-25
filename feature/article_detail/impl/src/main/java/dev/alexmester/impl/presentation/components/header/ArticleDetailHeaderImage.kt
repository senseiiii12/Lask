package dev.alexmester.impl.presentation.components.header

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import dev.alexmester.ui.desing_system.LaskColors
import dev.alexmester.ui.shared_transition.sharedElementIfAvailable

@Composable
internal fun ArticleDetailHeaderImage(
    modifier: Modifier = Modifier,
    image: String?,
    articleId: Long,
) {
    val statusBarHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val gradientAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        gradientAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 300),
        )
    }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(280.dp + statusBarHeight)
    ) {
        if (image != null) {
            Box(
                modifier = modifier.sharedElementIfAvailable(key = "image_$articleId")
            ){
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(image)
                        .crossfade(false)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize(),
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer { alpha = gradientAlpha.value }
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    MaterialTheme.LaskColors.backgroundPrimary,
                                ),
                                startY = 300f,
                            )
                        )
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .sharedElementIfAvailable(key = "image_$articleId")
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                MaterialTheme.LaskColors.brand_blue,
                                MaterialTheme.LaskColors.backgroundPrimary,
                            )
                        )
                    )
            )
        }
    }
}