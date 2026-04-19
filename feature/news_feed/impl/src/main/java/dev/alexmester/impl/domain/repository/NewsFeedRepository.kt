package dev.alexmester.impl.domain.repository

import dev.alexmester.models.news.NewsCluster
import dev.alexmester.models.result.AppResult
import kotlinx.coroutines.flow.Flow

interface NewsFeedRepository {

    fun observeFeedClusters(): Flow<List<NewsCluster>>

    fun observeReadArticleIds(): Flow<List<Long>>

    suspend fun refreshFeed(
        country: String,
        language: String,
    ): AppResult<Int>

    suspend fun getLastCachedAt(): Long?
}