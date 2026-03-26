package dev.alexmester.newsfeed.impl.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import dev.alexmester.models.news.NewsArticle
import dev.alexmester.ui.desing_system.LaskColors
import dev.alexmester.ui.desing_system.LaskTypography

@Composable
fun NewsFeedArticleCard(
    article: NewsArticle,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .clickable(onClick = onClick)
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.fillMaxHeight().weight(1f),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.Start
        ) {
            Row {
                Text(
                    modifier = Modifier.padding(bottom = 12.dp),
                    text = article.title,
                    style = MaterialTheme.LaskTypography.h5,
                    color = MaterialTheme.LaskColors.textPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                article.sentiment?.let { sentiment ->
                    SentimentIndicator(sentiment = sentiment)
                }

                Text(
                    text = article.publishDate.take(10),
                    style = MaterialTheme.LaskTypography.footnote,
                    color = MaterialTheme.LaskColors.textSecondary,
                )

                article.authors.firstOrNull()?.let { author ->
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

        article.image?.let { imageUrl ->
            Spacer(modifier = Modifier.width(12.dp))
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = article.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp)),
            )
        }
    }

}

@Composable
private fun SentimentIndicator(sentiment: Double) {
    val color = when {
        sentiment > 0.1 -> Color(0xFF1DB845)   // позитив — зелёный
        sentiment < -0.1 -> Color(0xFFD32F2F)  // негатив — красный
        else -> Color(0xFFFF9800)              // нейтральный — оранжевый
    }
    Text(
        text = "●",
        color = color,
        style = MaterialTheme.LaskTypography.footnote,
    )
}




