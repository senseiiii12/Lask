package dev.alexmester.impl.data.local

import dev.alexmester.database.dao.ArticleDao
import dev.alexmester.database.dao.ArticleUserStateDao
import dev.alexmester.database.entity.ArticleEntity
import dev.alexmester.database.entity.ArticleUserStateEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class ArticleDetailLocalDataSource(
    private val articleDao: ArticleDao,
    private val userStateDao: ArticleUserStateDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    // ── Article ───────────────────────────────────────────────────────────────

    suspend fun getArticleById(id: Long): ArticleEntity? =
        withContext(ioDispatcher) { articleDao.getArticleById(id) }

    // ── UserState helpers ─────────────────────────────────────────────────────

    /**
     * Гарантируем существование строки user state перед любым UPDATE.
     * Вызываем перед bookmark/clap/read операциями.
     */
    private suspend fun ensureUserState(articleId: Long) {
        userStateDao.insertIfAbsent(ArticleUserStateEntity(articleId = articleId))
    }

    // ── Bookmark ──────────────────────────────────────────────────────────────

    fun observeIsBookmarked(id: Long): Flow<Boolean> =
        userStateDao.observeIsBookmarked(id).map { it ?: false }

    suspend fun isBookmarked(id: Long): Boolean =
        withContext(ioDispatcher) {
            userStateDao.isBookmarked(id) ?: false
        }

    /**
     * Возвращает новое состояние закладки после переключения.
     */
    suspend fun toggleBookmark(articleId: Long): Boolean =
        withContext(ioDispatcher) {
            ensureUserState(articleId)
            val current = userStateDao.isBookmarked(articleId) ?: false
            val newState = !current
            val bookmarkedAt = if (newState) System.currentTimeMillis() else null
            userStateDao.updateBookmark(
                articleId = articleId,
                isBookmarked = newState,
                bookmarkedAt = bookmarkedAt,
            )
            newState
        }

    // ── Clap ──────────────────────────────────────────────────────────────────

    fun observeClapCount(id: Long): Flow<Int> =
        userStateDao.observeClapCount(id).map { it ?: 0 }

    suspend fun getClapCount(id: Long): Int =
        withContext(ioDispatcher) {
            userStateDao.getClapCount(id) ?: 0
        }

    suspend fun addClap(articleId: Long) =
        withContext(ioDispatcher) {
            ensureUserState(articleId)
            userStateDao.incrementClap(articleId)
        }

    // ── Read ──────────────────────────────────────────────────────────────────

    suspend fun markAsRead(articleId: Long) =
        withContext(ioDispatcher) {
            ensureUserState(articleId)
            userStateDao.markAsRead(
                articleId = articleId,
                readAt = System.currentTimeMillis(),
            )
        }
}