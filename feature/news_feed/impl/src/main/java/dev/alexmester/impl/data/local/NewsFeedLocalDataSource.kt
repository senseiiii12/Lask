package dev.alexmester.impl.data.local

import androidx.room.withTransaction
import dev.alexmester.database.AppDatabase
import dev.alexmester.database.dao.ArticleDao
import dev.alexmester.database.dao.ArticleUserStateDao
import dev.alexmester.database.dao.FeedCacheDao
import dev.alexmester.database.entity.ArticleEntity
import dev.alexmester.database.entity.FeedCacheEntity
import dev.alexmester.database.entity.FeedCacheEntity.Companion.FEED_TOP
import dev.alexmester.impl.data.mapper.toClusters
import dev.alexmester.models.news.NewsCluster
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class NewsFeedLocalDataSource(
    private val db: AppDatabase,
    private val articleDao: ArticleDao,
    private val feedCacheDao: FeedCacheDao,
    private val userStateDao: ArticleUserStateDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {

    /**
     * Flow кластеров ленты с актуальным user state.
     * Обновляется автоматически при любом изменении feed_cache,
     * articles или article_user_state.
     */
    fun observeFeedClusters(): Flow<List<NewsCluster>> =
        feedCacheDao.observeFeedWithState(FEED_TOP)
            .map { rows -> rows.toClusters() }

    fun observeReadArticleIds(): Flow<List<Long>> =
        userStateDao.observeReadArticleIds()

    suspend fun getLastCachedAt(): Long? =
        withContext(ioDispatcher) {
            feedCacheDao.getLastCachedAt(FEED_TOP)
        }

    /**
     * Атомарная замена ленты:
     * 1. Удаляем старый feed_cache для FEED_TOP
     * 2. Вставляем новые articles с IGNORE (не затираем существующие)
     * 3. Вставляем новый feed_cache с REPLACE
     * 4. Удаляем orphaned articles (нет в cache и нет активного user state)
     *
     * Всё в одной транзакции — UI никогда не увидит пустую ленту.
     */
    suspend fun replaceFeedCache(
        articles: List<ArticleEntity>,
        feedCache: List<FeedCacheEntity>,
    ) = withContext(ioDispatcher) {
        db.withTransaction {
            feedCacheDao.clearFeed(FEED_TOP)
            articleDao.insertArticles(articles)
            feedCacheDao.insertFeedCache(feedCache)
            articleDao.deleteOrphaned()
        }
    }
}