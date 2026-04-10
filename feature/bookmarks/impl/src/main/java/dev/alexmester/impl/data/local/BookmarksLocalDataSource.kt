package dev.alexmester.impl.data.local

import dev.alexmester.database.dao.ArticleUserStateDao
import dev.alexmester.database.entity.ArticleEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class BookmarksLocalDataSource(
    private val userStateDao: ArticleUserStateDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    /**
     * Flow всех закладок — статьи отсортированы по времени добавления (новые первыми).
     * JOIN происходит в БД, возвращаем сразу ArticleEntity.
     */
    fun observeBookmarkedArticles(): Flow<List<ArticleEntity>> =
        userStateDao.observeBookmarkedArticles()

    /**
     * Снимаем флаг isBookmarked — статья остаётся в articles,
     * если у неё есть isRead или clapCount > 0.
     * Физически удалится только через ArticleDao.deleteOrphaned().
     */
    suspend fun removeBookmarks(ids: Set<Long>) = withContext(ioDispatcher) {
        ids.forEach { articleId ->
            userStateDao.updateBookmark(
                articleId = articleId,
                isBookmarked = false,
                bookmarkedAt = null,
            )
        }
    }
}