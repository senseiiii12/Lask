package dev.alexmester.impl.presentation.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.alexmester.newsfeed.impl.presentation.components.NewsFeedClusterHeader
import dev.alexmester.newsfeed.impl.presentation.components.NewsFeedArticleCard
import dev.alexmester.newsfeed.impl.presentation.components.NewsFeedOfflineBanner
import dev.alexmester.newsfeed.impl.presentation.feed.ContentState
import dev.alexmester.newsfeed.impl.presentation.feed.NewsFeedScreenState

@Composable
fun NewsFeedList(
    modifier: Modifier = Modifier,
    state: NewsFeedScreenState.Content,
    onClickArticle: (articleId: Long, articleUrl: String) -> Unit,
) {
    LazyColumn(
        contentPadding = PaddingValues(bottom = 16.dp),
        modifier = modifier.fillMaxSize(),
    ) {

        if (state.contentState is ContentState.Offline) {
            item(key = "offline_banner") {
                NewsFeedOfflineBanner(
                    lastCachedAt = state.contentState.lastCachedAt
                )
            }
        }

        state.clusters.forEach { cluster ->
            stickyHeader(key = "header_${cluster.id}") {
                NewsFeedClusterHeader(
                    title = cluster.leadArticle.title
                )
            }
            items(
                items = cluster.articles,
                key = { it.id },
            ) { article ->
                val isLast = article == cluster.articles.last()
                NewsFeedArticleCard(
                    article = article,
                    onClick = { onClickArticle(article.id, article.url,)},
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            top = 4.dp,
                            bottom = if (isLast) 16.dp else 4.dp
                        ),
                )
            }
        }
    }
}