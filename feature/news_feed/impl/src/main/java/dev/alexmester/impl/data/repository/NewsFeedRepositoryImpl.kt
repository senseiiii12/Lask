package dev.alexmester.impl.data.repository

import android.util.Log
import dev.alexmester.database.entity.NewsArticleEntity.Companion.SOURCE_FEED
import dev.alexmester.impl.data.local.NewsFeedLocalDataSource
import dev.alexmester.impl.data.mapper.dtosToCluster
import dev.alexmester.impl.data.mapper.entitiesToClusters
import dev.alexmester.impl.data.mapper.toEntities
import dev.alexmester.impl.data.remote.NewsFeedApiService
import dev.alexmester.impl.domain.repository.NewsFeedRepository
import dev.alexmester.models.news.NewsCluster
import dev.alexmester.models.result.AppResult
import dev.alexmester.network.ext.safeApiCall
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NewsFeedRepositoryImpl(
    private val remote: NewsFeedApiService,
    private val local: NewsFeedLocalDataSource,
) : NewsFeedRepository {

    override fun getClustersFlow(): Flow<List<NewsCluster>> =
        local.getArticles().map { it.entitiesToClusters() }

    override fun getReadArticleIdsFlow(): Flow<List<Long>> =
        local.getReadArticleIds()

    override suspend fun refreshTopNews(
        country: String,
        language: String,
    ): AppResult<Unit> = safeApiCall {
        val response = remote.getTopNews(sourceCountry = country, language = language)
        Log.d("NewsFeed", "API clusters count: ${response.topNews.size}")

        val clusters = response.topNews.dtosToCluster()
        Log.d("NewsFeed", "Domain clusters count: ${clusters.size}")

        val entities = clusters.toEntities(SOURCE_FEED)
        Log.d("NewsFeed", "Entities to save: ${entities.size}")

        local.replaceArticles(entities)
        Log.d("NewsFeed", "Saved to Room")
    }

    override suspend fun getLastCachedAt(): Long? =
        local.getLastCachedAt()
}