package dev.alexmester.ui.components.list_card.components

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.ImageNotSupported
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import dev.alexmester.ui.R
import dev.alexmester.ui.components.list_card.rememberLaskArticleCardPainter
import dev.alexmester.ui.desing_system.LaskColors
import dev.alexmester.ui.desing_system.LaskTypography
import dev.alexmester.ui.shared_transition.sharedElementIfAvailable

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun ArticleCardImage(
    modifier: Modifier = Modifier,
    imageUrl: String?,
    title: String,
    articleId: Long,
    isRead: Boolean,
) {

    val backgroundColor = MaterialTheme.LaskColors.brand_blue10
    val iconTint = MaterialTheme.LaskColors.textSecondary.copy(alpha = 0.4f)

    val placeholderPainter = rememberLaskArticleCardPainter(
        backgroundColor = backgroundColor,
        iconTint = iconTint,
        icon = Icons.Default.Image,
    )
    val errorPainter = rememberLaskArticleCardPainter(
        backgroundColor = backgroundColor,
        iconTint = iconTint,
        icon = Icons.Default.BrokenImage,
    )
    val emptyPainter = rememberLaskArticleCardPainter(
        backgroundColor = backgroundColor,
        iconTint = iconTint,
        icon = Icons.Default.ImageNotSupported,
    )

    val sizeMod = modifier
        .sharedElementIfAvailable(key = "image_$articleId")
        .clip(RoundedCornerShape(8.dp))

    Box(modifier = sizeMod) {
        if (imageUrl != null) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl)
                    .crossfade(true)
                    .placeholderMemoryCacheKey(imageUrl)
                    .build(),
                contentDescription = title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                placeholder = placeholderPainter,
                error = errorPainter,
            )
        } else {
            Image(
                modifier = sizeMod,
                painter = emptyPainter,
                contentDescription = null
            )
        }
        if (isRead) {
            Text(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .background(
                        color = MaterialTheme.LaskColors.brand_blue10.copy(alpha = 0.8f),
                        shape = RoundedCornerShape(topEnd = 8.dp, bottomStart = 8.dp)
                    )
                    .padding(vertical = 4.dp, horizontal = 8.dp),
                text = stringResource(R.string.article_read),
                style = MaterialTheme.LaskTypography.footnote,
                color = MaterialTheme.LaskColors.textPrimary
            )
        }
    }
}