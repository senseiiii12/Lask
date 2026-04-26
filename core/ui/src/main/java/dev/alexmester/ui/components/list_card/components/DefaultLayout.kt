package dev.alexmester.ui.components.list_card.components

import android.R.attr.layout
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.LookaheadScope
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import dev.alexmester.models.news.NewsArticle
import dev.alexmester.ui.components.buttons.BookmarkButtonStyle
import dev.alexmester.ui.components.buttons.LaskBookmarkButton
import kotlin.math.roundToInt

@Composable
internal fun DefaultLayout(
    modifier: Modifier,
    article: NewsArticle,
    isRead: Boolean,
    selectionMode: Boolean,
    isKept: Boolean,
    onBookmarkToggle: () -> Unit,
    onClick: () -> Unit,
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
            ArticleTitle(article)
            ArticleMeta(article)
        }

        ArticleCardImage(
            modifier = Modifier
                .width(112.dp)
                .height(80.dp),
            imageUrl = article.image,
            title = article.title,
            articleId = article.id,
            isRead = isRead
        )

        Box(
            modifier = Modifier
                .animateContentSize()
                .width(if (selectionMode) 40.dp else 0.dp)
                .fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            LaskBookmarkButton(
                isBookmarked = isKept,
                onClick = onBookmarkToggle,
                style = BookmarkButtonStyle.Standalone,
            )
        }
    }
}