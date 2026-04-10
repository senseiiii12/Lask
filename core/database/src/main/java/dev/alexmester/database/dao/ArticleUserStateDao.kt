package dev.alexmester.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.alexmester.database.entity.ArticleEntity
import dev.alexmester.database.entity.ArticleUserStateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ArticleUserStateDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIfAbsent(state: ArticleUserStateEntity)

    // ── Bookmark ──────────────────────────────────────────────────────────────

    @Query("""
        UPDATE article_user_state
        SET isBookmarked = :isBookmarked, bookmarkedAt = :bookmarkedAt
        WHERE articleId = :articleId
    """)
    suspend fun updateBookmark(articleId: Long, isBookmarked: Boolean, bookmarkedAt: Long?)

    @Query("SELECT isBookmarked FROM article_user_state WHERE articleId = :articleId")
    fun observeIsBookmarked(articleId: Long): Flow<Boolean?>

    @Query("SELECT isBookmarked FROM article_user_state WHERE articleId = :articleId")
    suspend fun isBookmarked(articleId: Long): Boolean?

    @Query("""
        SELECT a.* FROM articles a
        INNER JOIN article_user_state s ON a.id = s.articleId
        WHERE s.isBookmarked = 1
        ORDER BY s.bookmarkedAt DESC
    """)
    fun observeBookmarkedArticles(): Flow<List<ArticleEntity>>

    // ── Read ──────────────────────────────────────────────────────────────────

    @Query("""
        UPDATE article_user_state
        SET isRead = 1, readAt = :readAt
        WHERE articleId = :articleId
    """)
    suspend fun markAsRead(articleId: Long, readAt: Long)

    @Query("SELECT isRead FROM article_user_state WHERE articleId = :articleId")
    fun observeIsRead(articleId: Long): Flow<Boolean?>

    @Query("SELECT articleId FROM article_user_state WHERE isRead = 1")
    fun observeReadArticleIds(): Flow<List<Long>>

    @Query("""
        SELECT a.* FROM articles a
        INNER JOIN article_user_state s ON a.id = s.articleId
        WHERE s.isRead = 1
        ORDER BY s.readAt DESC
    """)
    fun observeReadArticles(): Flow<List<ArticleEntity>>

    // ── Clap ──────────────────────────────────────────────────────────────────

    @Query("""
        UPDATE article_user_state
        SET clapCount = clapCount + 1
        WHERE articleId = :articleId
    """)
    suspend fun incrementClap(articleId: Long)

    @Query("SELECT clapCount FROM article_user_state WHERE articleId = :articleId")
    fun observeClapCount(articleId: Long): Flow<Int?>

    @Query("SELECT clapCount FROM article_user_state WHERE articleId = :articleId")
    suspend fun getClapCount(articleId: Long): Int?

    @Query("""
        SELECT a.* FROM articles a
        INNER JOIN article_user_state s ON a.id = s.articleId
        WHERE s.clapCount > 0
        ORDER BY s.clapCount DESC
    """)
    fun observeClappedArticles(): Flow<List<ArticleEntity>>
}
