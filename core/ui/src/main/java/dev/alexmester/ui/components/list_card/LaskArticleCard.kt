package dev.alexmester.ui.components.list_card

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import dev.alexmester.ui.R
import dev.alexmester.ui.desing_system.LaskColors
import dev.alexmester.ui.desing_system.LaskPalette
import dev.alexmester.ui.desing_system.LaskTypography
import dev.alexmester.ui.transition.sharedElementIfAvailable


@Composable
fun LaskArticleCard(
    modifier: Modifier = Modifier,
    title: String,
    imageUrl: String?,
    category: String?,
    publishDate: String,
    authors: List<String?>,
    sentiment: Double?,
    articleId: Long,
    selectionMode: Boolean = false,
    isKept: Boolean = true,
    isRead: Boolean = false,
    onBookmarkToggle: () -> Unit = {},
    onClick: () -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = title,
                style = MaterialTheme.LaskTypography.h5,
                color = MaterialTheme.LaskColors.textPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .sharedElementIfAvailable(key = "title_$articleId")
                    .padding(bottom = 8.dp),
            )

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                category?.let {
                    Text(
                        text = it.replaceFirstChar { c -> c.uppercase() },
                        style = MaterialTheme.LaskTypography.footnote,
                        color = MaterialTheme.LaskColors.informative,
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    sentiment?.let { SentimentDot(it) }
                    Text(
                        text = publishDate,
                        style = MaterialTheme.LaskTypography.footnote,
                        color = MaterialTheme.LaskColors.textSecondary,
                    )
                    authors.firstOrNull()?.let { author ->
                        Text(
                            text = "· $author",
                            style = MaterialTheme.LaskTypography.footnote,
                            color = MaterialTheme.LaskColors.textSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }
        }

        ArticleImage(
            imageUrl = imageUrl,
            title = title,
            articleId = articleId,
            isRead = isRead
        )

        AnimatedVisibility(
            visible = selectionMode,
            enter = fadeIn() + scaleIn(initialScale = 0.7f),
            exit = fadeOut() + scaleOut(targetScale = 0.7f),
        ) {
            BookmarkIcon(
                isKept = isKept,
                onToggle = onBookmarkToggle,
            )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun ArticleImage(
    imageUrl: String?,
    title: String,
    articleId: Long,
    isRead: Boolean,
) {

    val sizeMod = Modifier
        .sharedElementIfAvailable(key = "image_$articleId")
        .width(112.dp)
        .height(80.dp)
        .clip(RoundedCornerShape(8.dp))

    if (imageUrl != null) {
        Box(modifier = sizeMod) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl)
                    .crossfade(true)
                    .placeholderMemoryCacheKey(imageUrl)
                    .build(),
                contentDescription = title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                placeholder = ColorPainter(MaterialTheme.LaskColors.backgroundSecondary),
                error = ColorPainter(MaterialTheme.LaskColors.backgroundSecondary),
            )
            if (isRead){
                Text(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .background(
                            color = MaterialTheme.LaskColors.brand_blue10.copy(alpha = 0.8f),
                            shape = RoundedCornerShape(topEnd = 8.dp, bottomStart = 8.dp)
                        )
                        .padding(vertical = 4.dp, horizontal = 8.dp)
                    ,
                    text = stringResource(R.string.reading_article),
                    style = MaterialTheme.LaskTypography.footnote,
                    color = MaterialTheme.LaskColors.textPrimary
                )
            }
        }
    } else {
        ArticleImagePlaceholder(modifier = sizeMod)
    }
}

@Composable
private fun ArticleImagePlaceholder(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(80.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.LaskColors.backgroundSecondary),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Default.Image,
            contentDescription = null,
            tint = MaterialTheme.LaskColors.textSecondary.copy(alpha = 0.4f),
            modifier = Modifier.size(28.dp),
        )
    }
}

@Composable
private fun BookmarkIcon(
    isKept: Boolean,
    onToggle: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isKept) 1f else 0.85f,
        animationSpec = spring(dampingRatio = 0.6f),
    )

    Icon(
        imageVector = if (isKept) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
        contentDescription = if (isKept) "Will be kept" else "Will be removed",
        tint = LaskPalette.Bookmark,
        modifier = Modifier
            .size(24.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(onClick = onToggle),
    )
}

@Composable
private fun SentimentDot(sentiment: Double) {
    val color = when {
        sentiment > 0.1 -> LaskPalette.Sentiment_Positive
        sentiment < -0.1 -> LaskPalette.Sentiment_Negative
        else -> LaskPalette.Sentiment_Neutral
    }
    Text(
        text = "●",
        color = color,
        style = MaterialTheme.LaskTypography.footnote,
    )
}