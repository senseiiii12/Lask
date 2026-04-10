package dev.alexmester.impl.data.repository

import dev.alexmester.impl.data.local.ArticleDetailLocalDataSource
import dev.alexmester.impl.data.mappers.toDomain
import dev.alexmester.impl.domain.repository.ArticleDetailRepository
import dev.alexmester.models.news.NewsArticle
import kotlinx.coroutines.flow.Flow

class ArticleDetailRepositoryImpl(
    private val local: ArticleDetailLocalDataSource,
) : ArticleDetailRepository {

    override suspend fun getArticleById(id: Long): NewsArticle? =
        local.getArticleById(id)?.toDomain()

    // ── Bookmark ──────────────────────────────────────────────────────────────

    override fun observeIsBookmarked(id: Long): Flow<Boolean> =
        local.observeIsBookmarked(id)

    override suspend fun isBookmarked(id: Long): Boolean =
        local.isBookmarked(id)

    override suspend fun toggleBookmark(articleId: Long): Boolean =
        local.toggleBookmark(articleId)

    // ── Clap ──────────────────────────────────────────────────────────────────

    override fun observeClapCount(id: Long): Flow<Int> =
        local.observeClapCount(id)

    override suspend fun getClapCount(id: Long): Int =
        local.getClapCount(id)

    override suspend fun addClap(articleId: Long) =
        local.addClap(articleId)

    // ── Read ──────────────────────────────────────────────────────────────────

    override suspend fun markAsRead(articleId: Long) =
        local.markAsRead(articleId)
}