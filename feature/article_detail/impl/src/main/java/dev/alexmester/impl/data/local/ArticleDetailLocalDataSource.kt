package dev.alexmester.impl.data.local

import dev.alexmester.database.dao.BookmarkDao
import dev.alexmester.database.dao.ClapDao
import dev.alexmester.database.dao.NewsArticleDao
import dev.alexmester.database.dao.ReadingHistoryDao
import dev.alexmester.database.entity.BookmarkEntity
import dev.alexmester.database.entity.ClapEntity
import dev.alexmester.database.entity.NewsArticleEntity
import dev.alexmester.database.entity.ReadingHistoryEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ArticleDetailLocalDataSource(
    private val newsArticleDao: NewsArticleDao,
    private val bookmarkDao: BookmarkDao,
    private val clapDao: ClapDao,
    private val readingHistoryDao: ReadingHistoryDao,
) {
    suspend fun getArticleById(id: Long): NewsArticleEntity? =
        newsArticleDao.getArticleById(id)

    suspend fun getBookmarkById(id: Long): BookmarkEntity? =
        bookmarkDao.getBookmarkById(id)

    fun isBookmarked(id: Long): Flow<Boolean> =
        bookmarkDao.isBookmarked(id)

    suspend fun insertBookmark(bookmark: BookmarkEntity) =
        bookmarkDao.insertBookmark(bookmark)

    suspend fun deleteBookmark(id: Long) =
        bookmarkDao.deleteBookmark(id)

    fun getClapFlow(id: Long): Flow<Int> =
        clapDao.getClapFlow(id).map { it?.count ?: 0 }

    suspend fun addClap(id: Long) {
        val existing = clapDao.getClapCount(id)
        if (existing == null) {
            clapDao.upsert(ClapEntity(articleId = id, count = 1))
        } else {
            clapDao.increment(id)
        }
    }
    suspend fun markAsRead(articleId: Long, articleTitle: String) {
        readingHistoryDao.upsert(
            ReadingHistoryEntity(
                articleId = articleId,
                articleTitle = articleTitle,
                readAt = System.currentTimeMillis(),
            )
        )
    }
}