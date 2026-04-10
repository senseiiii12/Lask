package dev.alexmester.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.alexmester.database.entity.ArticleEntity
import dev.alexmester.database.entity.FeedCacheEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FeedCacheDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeedCache(entries: List<FeedCacheEntity>)

    @Query("DELETE FROM feed_cache WHERE feedType = :feedType")
    suspend fun clearFeed(feedType: String)

    /**
     * Возвращаем статьи ленты с их user state через LEFT JOIN.
     * Порядок: кластер → позиция внутри кластера.
     */
    @Query("""
        SELECT a.*, 
               f.clusterId, 
               f.position,
               s.isBookmarked,
               s.isRead,
               s.clapCount
        FROM feed_cache f
        INNER JOIN articles a ON f.articleId = a.id
        LEFT JOIN article_user_state s ON a.id = s.articleId
        WHERE f.feedType = :feedType
        ORDER BY f.clusterId ASC, f.position ASC
    """)
    fun observeFeedWithState(feedType: String): Flow<List<FeedArticleWithState>>

    @Query("SELECT MAX(cachedAt) FROM feed_cache WHERE feedType = :feedType")
    suspend fun getLastCachedAt(feedType: String): Long?
}

/**
 * Проекция результата JOIN запроса.
 * Room умеет маппить flat результат в data class без @Embedded/@Relation.
 */
data class FeedArticleWithState(
    // ArticleEntity fields
    val id: Long,
    val title: String,
    val text: String?,
    val summary: String?,
    val url: String,
    val image: String?,
    val video: String?,
    val publishDate: String,
    val authors: String,
    val category: String?,
    val language: String?,
    val sourceCountry: String?,
    val sentiment: Double?,
    // FeedCacheEntity fields
    val clusterId: Int,
    val position: Int,
    // ArticleUserStateEntity fields (nullable — LEFT JOIN)
    val isBookmarked: Boolean?,
    val isRead: Boolean?,
    val clapCount: Int?,
)