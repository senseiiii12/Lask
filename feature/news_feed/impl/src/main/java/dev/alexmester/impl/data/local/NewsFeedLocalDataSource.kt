package dev.alexmester.impl.data.local

import android.util.Log
import androidx.room.withTransaction
import dev.alexmester.database.AppDatabase
import dev.alexmester.database.dao.NewsArticleDao
import dev.alexmester.database.entity.NewsArticleEntity
import dev.alexmester.database.entity.NewsArticleEntity.Companion.SOURCE_FEED
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach

class NewsFeedLocalDataSource(
    private val dao: NewsArticleDao,
    private val db: AppDatabase,
) {

    fun getArticles(): Flow<List<NewsArticleEntity>> =
        dao.getArticlesBySource(SOURCE_FEED)

    suspend fun getLastCachedAt(): Long? =
        dao.getLastCachedAt(SOURCE_FEED)


    suspend fun replaceArticles(articles: List<NewsArticleEntity>) =
        db.withTransaction {
            dao.clearBySource(SOURCE_FEED)
            dao.insertArticles(articles)
        }

    fun getReadArticleIds(): Flow<List<Long>> =
        dao.getReadArticleIdsBySource(SOURCE_FEED)
}