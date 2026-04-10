package dev.alexmester.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.alexmester.database.entity.NewsArticleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NewsArticleDao {

    @Query("SELECT * FROM news_articles WHERE sourceScreen = :source ORDER BY publishDate DESC")
    fun getArticlesBySource(source: String): Flow<List<NewsArticleEntity>>

    @Query("SELECT * FROM news_articles WHERE id = :id")
    suspend fun getArticleById(id: Long): NewsArticleEntity?

    @Query("SELECT MAX(cachedAt) FROM news_articles WHERE sourceScreen = :source")
    suspend fun getLastCachedAt(source: String): Long?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticles(articles: List<NewsArticleEntity>)

    @Query("DELETE FROM news_articles WHERE sourceScreen = :source")
    suspend fun clearBySource(source: String)

    @Query("DELETE FROM news_articles")
    suspend fun clearAll()
}
