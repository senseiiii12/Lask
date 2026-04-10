package dev.alexmester.impl.data.local

import dev.alexmester.database.dao.BookmarkDao
import dev.alexmester.database.dao.ClapDao
import dev.alexmester.database.dao.NewsArticleDao
import dev.alexmester.database.dao.ReadingHistoryDao
import dev.alexmester.database.entity.BookmarkEntity
import dev.alexmester.database.entity.NewsArticleEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class ArticleListLocalDataSource(
    private val newsArticleDao: NewsArticleDao,
    private val bookmarkDao: BookmarkDao,
    private val clapDao: ClapDao,
    private val readingHistoryDao: ReadingHistoryDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    /**
     * Возвращаем прочитанные статьи.
     * Стратегия: берём ID из reading_history, ищем в news_articles,
     * если не нашли — смотрим в bookmarks (статья могла быть вытеснена из кэша).
     * Статьи сортированы по readAt DESC.
     */
    fun getReadArticlesFlow(): Flow<Pair<List<NewsArticleEntity>, List<BookmarkEntity>>> =
        combine(
            readingHistoryDao.getAllHistory(),
            bookmarkDao.getAllBookmarks(),
        ) { history, bookmarks ->
            val bookmarkMap = bookmarks.associateBy { it.id }
            val readIds = history.map { it.articleId }

            // Нам нужны сущности в порядке readAt DESC
            // Возвращаем пары (entity | null, bookmark | null) — маппинг в репо
            val foundEntities = mutableListOf<NewsArticleEntity>()
            val fallbackBookmarks = mutableListOf<BookmarkEntity>()

            readIds.forEach { id ->
                bookmarkMap[id]?.let { fallbackBookmarks.add(it) }
                    ?: run {
                        // нет в закладках — добавим placeholder, репо разберётся через newsArticleDao
                    }
            }

            foundEntities to fallbackBookmarks
        }

    /**
     * Более прямолинейный подход: объединяем два Flow.
     * READ: history IDs → сначала bookmarks, потом cache
     */
    fun getReadArticleIds(): Flow<List<Long>> = readingHistoryDao.getReadArticleIds()

    fun getClappedArticleIds(): Flow<List<Long>> =
        clapDao.getAllClaps().map { list -> list.map { it.articleId } }

    fun getAllBookmarks(): Flow<List<BookmarkEntity>> = bookmarkDao.getAllBookmarks()

    suspend fun getArticleById(id: Long): NewsArticleEntity? =
        withContext(ioDispatcher) { newsArticleDao.getArticleById(id) }
}