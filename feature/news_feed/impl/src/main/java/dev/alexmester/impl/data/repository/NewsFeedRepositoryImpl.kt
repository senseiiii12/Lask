package dev.alexmester.impl.data.repository

import dev.alexmester.database.entity.FeedCacheEntity.Companion.FEED_TOP
import dev.alexmester.impl.data.local.NewsFeedLocalDataSource
import dev.alexmester.impl.data.mapper.toEntities
import dev.alexmester.impl.data.remote.NewsFeedApiService
import dev.alexmester.impl.domain.repository.NewsFeedRepository
import dev.alexmester.models.news.NewsCluster
import dev.alexmester.models.result.AppResult
import dev.alexmester.network.ext.safeApiCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class NewsFeedRepositoryImpl(
    private val remote: NewsFeedApiService,
    private val local: NewsFeedLocalDataSource,
) : NewsFeedRepository {

    override fun observeFeedClusters(): Flow<List<NewsCluster>> =
        local.observeFeedClusters()

    override fun observeReadArticleIds() =
        local.observeReadArticleIds()

    override suspend fun refreshFeed(
        country: String,
        language: String,
    ): AppResult<Int> = safeApiCall {
        val response = remote.getTopNews(sourceCountry = country, language = language)

        val (articles, feedCache) = withContext(Dispatchers.Default) {
            response.topNews.toEntities(FEED_TOP)
        }

        local.replaceFeedCache(articles = articles, feedCache = feedCache)
        response.topNews.size
    }

    override suspend fun getLastCachedAt(): Long? =
        local.getLastCachedAt()
}