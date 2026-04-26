package dev.alexmester.impl

import dev.alexmester.impl.domain.repository.NewsFeedRepository
import dev.alexmester.models.news.NewsArticle
import dev.alexmester.models.news.NewsCluster
import dev.alexmester.models.result.AppResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeNewsFeedRepository : NewsFeedRepository {

    private val _clusters = MutableStateFlow<List<NewsCluster>>(emptyList())
    private val _readIds = MutableStateFlow<List<Long>>(emptyList())

    var refreshResult: AppResult<Int> = AppResult.Success(10)
    var lastCachedAt: Long? = null
    var currentLocale: Pair<String, String> = "us" to "en"
    var refreshCallCount: Int = 0
        private set

    var lastRefreshCountry: String? = null
        private set

    var lastRefreshLanguage: String? = null
        private set

    fun emitClusters(clusters: List<NewsCluster>) {
        _clusters.value = clusters
    }

    fun emitReadIds(ids: List<Long>) {
        _readIds.value = ids
    }

    override fun observeFeedClusters(): Flow<List<NewsCluster>> = _clusters.asStateFlow()

    override fun observeReadArticleIds(): Flow<List<Long>> = _readIds.asStateFlow()

    override suspend fun refreshFeed(country: String, language: String): AppResult<Int> {
        refreshCallCount++
        lastRefreshCountry = country
        lastRefreshLanguage = language
        return refreshResult
    }

    override suspend fun getLastCachedAt(): Long? = lastCachedAt

    override suspend fun getCurrentLocale(): Pair<String, String>  = currentLocale
}

// ── Test builders ─────────────────────────────────────────────────────────────

fun buildArticle(id: Long = 1L) = NewsArticle(
    id = id,
    title = "Title $id",
    text = null,
    summary = null,
    url = "https://example.com/$id",
    image = null,
    video = null,
    publishDate = "2026-04-26",
    authors = emptyList(),
    category = null,
    language = "en",
    sourceCountry = "us",
    sentiment = null,
)

fun buildCluster(id: Int, articleCount: Int = 2) = NewsCluster(
    id = id,
    articles = (1..articleCount).map { buildArticle(id.toLong() * 10 + it) },
)