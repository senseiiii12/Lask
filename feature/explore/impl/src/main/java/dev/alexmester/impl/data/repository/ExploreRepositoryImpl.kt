package dev.alexmester.impl.data.repository

import dev.alexmester.database.entity.FeedCacheEntity.Companion.EXPLORE_FEED
import dev.alexmester.impl.data.local.ExploreLocalDataSource
import dev.alexmester.impl.data.mapper.toEntities
import dev.alexmester.impl.data.remote.ExploreApiService
import dev.alexmester.impl.domain.repository.ExploreRepository
import dev.alexmester.models.news.NewsArticle
import dev.alexmester.models.result.AppResult
import dev.alexmester.network.ext.safeApiCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class ExploreRepositoryImpl(
    private val remote: ExploreApiService,
    private val local: ExploreLocalDataSource,
) : ExploreRepository {

    override fun observeArticles(): Flow<List<NewsArticle>> = local.observeFeedArticles()

    override fun observeReadArticleIds(): Flow<List<Long>> = local.observeReadArticleIds()

    override suspend fun refresh(
        query: String,
        language: String,
        pageSize: Int,
    ): AppResult<Int> = safeApiCall {
        val response = remote.searchNews(
            text = query,
            language = language,
            offset = 0,
            number = pageSize,
        )

        val (articles, cache) = withContext(Dispatchers.Default) {
            response.news.toEntities(feedType = EXPLORE_FEED, positionStart = 0)
        }
        local.replaceFeed(articles = articles, feedCache = cache)
        response.news.size
    }

    override suspend fun loadMore(
        query: String,
        language: String,
        pageSize: Int,
        offset: Int,
    ): AppResult<Int> = safeApiCall {
        val response = remote.searchNews(
            text = query,
            language = language,
            offset = offset,
            number = pageSize,
        )

        val (articles, cache) = withContext(Dispatchers.Default) {
            response.news.toEntities(feedType = EXPLORE_FEED, positionStart = offset)
        }
        local.appendFeed(articles = articles, feedCache = cache)
        response.news.size
    }

    override suspend fun getLastCachedAt(): Long? = local.getLastCachedAt()
}