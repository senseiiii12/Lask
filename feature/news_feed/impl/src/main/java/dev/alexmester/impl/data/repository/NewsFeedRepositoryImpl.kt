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

    override fun getClustersFlow(): Flow<List<NewsCluster>> =
        local.observeFeedClusters()

    override fun getReadArticleIdsFlow() =
        local.observeReadArticleIds()

    override suspend fun refreshTopNews(
        country: String,
        language: String,
    ): AppResult<Unit> = safeApiCall {
        val response = remote.getTopNews(sourceCountry = country, language = language)

        val (articles, feedCache) = withContext(Dispatchers.Default) {
            response.topNews.toEntities(FEED_TOP)
        }

        local.replaceTopNewsFeed(articles = articles, feedCache = feedCache)
    }

    override suspend fun getLastCachedAt(): Long? =
        local.getLastCachedAt()
}