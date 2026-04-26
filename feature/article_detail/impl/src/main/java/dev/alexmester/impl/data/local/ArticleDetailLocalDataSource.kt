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

    suspend fun getArticleById(id: Long): ArticleEntity? =
        withContext(ioDispatcher) { articleDao.getArticleById(id) }

    private suspend fun ensureUserState(articleId: Long) {
        userStateDao.insertIfAbsent(ArticleUserStateEntity(articleId = articleId))
    }

    fun observeIsBookmarked(id: Long): Flow<Boolean> =
        userStateDao.observeIsBookmarked(id).map { it ?: false }

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

    fun observeClapCount(id: Long): Flow<Int> =
        userStateDao.observeClapCount(id).map { it ?: 0 }

    suspend fun addClap(articleId: Long) =
        withContext(ioDispatcher) {
            ensureUserState(articleId)
            userStateDao.incrementClap(articleId)
        }

    suspend fun markAsRead(articleId: Long) =
        withContext(ioDispatcher) {
            ensureUserState(articleId)
            userStateDao.markAsRead(
                articleId = articleId,
                readAt = System.currentTimeMillis(),
            )
        }
}