package dev.alexmester.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import dev.alexmester.database.entity.NewsArticleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NewsArticleDao {

    // ── Чтение ────────────────────────────────────────────────────────────────

    @Query("SELECT * FROM news_articles WHERE sourceScreen = :source ORDER BY publishDate DESC")
    fun getArticlesBySource(source: String): Flow<List<NewsArticleEntity>>

    @Query("SELECT * FROM news_articles WHERE id = :id")
    suspend fun getArticleById(id: Long): NewsArticleEntity?

    @Query("SELECT MAX(cachedAt) FROM news_articles WHERE sourceScreen = :source")
    suspend fun getLastCachedAt(source: String): Long?

    // ── Запись ────────────────────────────────────────────────────────────────

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticles(articles: List<NewsArticleEntity>)

    // ── Очистка ───────────────────────────────────────────────────────────────

    @Query("DELETE FROM news_articles WHERE sourceScreen = :source")
    suspend fun clearBySource(source: String)

    @Query("DELETE FROM news_articles")
    suspend fun clearAll()

    @Query("""
        SELECT n.id FROM news_articles n
        INNER JOIN reading_history r ON n.id = r.articleId
        WHERE n.sourceScreen = :source
    """)
    fun getReadArticleIdsBySource(source: String): Flow<List<Long>>
}
