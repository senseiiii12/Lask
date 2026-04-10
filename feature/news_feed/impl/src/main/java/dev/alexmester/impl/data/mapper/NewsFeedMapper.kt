package dev.alexmester.impl.data.mapper

import dev.alexmester.database.dao.FeedArticleWithState
import dev.alexmester.database.entity.ArticleEntity
import dev.alexmester.database.entity.FeedCacheEntity
import dev.alexmester.impl.data.remote.dto.NewsArticleDto
import dev.alexmester.impl.data.remote.dto.NewsClusterDto
import dev.alexmester.models.news.NewsArticle
import dev.alexmester.models.news.NewsCluster
import kotlinx.serialization.json.Json

private val json = Json { ignoreUnknownKeys = true }

// ── DTO → ArticleEntity ───────────────────────────────────────────────────────

fun NewsArticleDto.toArticleEntity(): ArticleEntity = ArticleEntity(
    id = id,
    title = title,
    text = text,
    summary = summary,
    url = url,
    image = image,
    video = video,
    publishDate = publishDate,
    authors = json.encodeToString(authors),
    category = category,
    language = language,
    sourceCountry = sourceCountry,
    sentiment = sentiment,
)

// ── DTO → FeedCacheEntity ─────────────────────────────────────────────────────

fun NewsArticleDto.toFeedCacheEntity(
    feedType: String,
    clusterId: Int,
    position: Int,
): FeedCacheEntity = FeedCacheEntity(
    feedType = feedType,
    articleId = id,
    clusterId = clusterId,
    position = position,
    cachedAt = System.currentTimeMillis(),
)

/**
 * Разворачиваем список кластеров в два плоских списка:
 * - все ArticleEntity (для вставки в articles с IGNORE)
 * - все FeedCacheEntity (для вставки в feed_cache с REPLACE)
 */
fun List<NewsClusterDto>.toEntities(feedType: String): Pair<List<ArticleEntity>, List<FeedCacheEntity>> {
    val articles = mutableListOf<ArticleEntity>()
    val feedCache = mutableListOf<FeedCacheEntity>()

    forEachIndexed { clusterIndex, cluster ->
        cluster.news.forEachIndexed { position, dto ->
            articles.add(dto.toArticleEntity())
            feedCache.add(dto.toFeedCacheEntity(feedType, clusterIndex, position))
        }
    }

    return articles to feedCache
}

// ── FeedArticleWithState → Domain ─────────────────────────────────────────────

fun FeedArticleWithState.toDomain(): NewsArticle = NewsArticle(
    id = id,
    title = title,
    text = text,
    summary = summary,
    url = url,
    image = image,
    video = video,
    publishDate = publishDate,
    authors = json.decodeFromString(authors),
    category = category,
    language = language,
    sourceCountry = sourceCountry,
    sentiment = sentiment,
)

/**
 * Группируем плоский список FeedArticleWithState обратно в кластеры.
 * Порядок сохранён через clusterId + position (ORDER BY в запросе).
 */
fun List<FeedArticleWithState>.toClusters(): List<NewsCluster> =
    groupBy { it.clusterId }
        .entries
        .sortedBy { it.key }
        .map { (clusterId, rows) ->
            NewsCluster(
                id = clusterId,
                articles = rows
                    .sortedBy { it.position }
                    .map { it.toDomain() },
            )
        }
        .filter { it.articles.isNotEmpty() }

// ── ArticleEntity → Domain ────────────────────────────────────────────────────

fun ArticleEntity.toDomain(): NewsArticle = NewsArticle(
    id = id,
    title = title,
    text = text,
    summary = summary,
    url = url,
    image = image,
    video = video,
    publishDate = publishDate,
    authors = json.decodeFromString(authors),
    category = category,
    language = language,
    sourceCountry = sourceCountry,
    sentiment = sentiment,
)