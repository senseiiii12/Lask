package dev.alexmester.impl.data.local

import androidx.room.withTransaction
import dev.alexmester.database.AppDatabase
import dev.alexmester.database.dao.ArticleDao
import dev.alexmester.database.dao.ArticleUserStateDao
import dev.alexmester.database.dao.FeedCacheDao
import dev.alexmester.database.entity.ArticleEntity
import dev.alexmester.database.entity.FeedCacheEntity
import dev.alexmester.database.entity.FeedCacheEntity.Companion.EXPLORE_FEED
import dev.alexmester.impl.data.mapper.toDomain
import dev.alexmester.models.news.NewsArticle
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class ExploreLocalDataSource(
    private val db: AppDatabase,
    private val articleDao: ArticleDao,
    private val feedCacheDao: FeedCacheDao,
    private val userStateDao: ArticleUserStateDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {

    fun observeFeedArticles(): Flow<List<NewsArticle>> =
        feedCacheDao.observeFeedWithState(EXPLORE_FEED)
            .map { rows -> rows.sortedBy { it.position }.map { it.toDomain() } }

    fun observeReadArticleIds(): Flow<List<Long>> = userStateDao.observeReadArticleIds()

    suspend fun getLastCachedAt(): Long? = withContext(ioDispatcher) {
        feedCacheDao.getLastCachedAt(EXPLORE_FEED)
    }

    suspend fun replaceFeed(
        articles: List<ArticleEntity>,
        feedCache: List<FeedCacheEntity>,
    ) = withContext(ioDispatcher) {
        db.withTransaction {
            feedCacheDao.clearFeed(EXPLORE_FEED)
            articleDao.insertArticles(articles)
            feedCacheDao.insertFeedCache(feedCache)
            articleDao.deleteOrphaned()
        }
    }

    suspend fun appendFeed(
        articles: List<ArticleEntity>,
        feedCache: List<FeedCacheEntity>,
    ) = withContext(ioDispatcher) {
        db.withTransaction {
            articleDao.insertArticles(articles)
            feedCacheDao.insertFeedCache(feedCache)
            articleDao.deleteOrphaned()
        }
    }
}