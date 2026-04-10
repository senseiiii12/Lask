package dev.alexmester.impl.domain.repository

import dev.alexmester.models.news.NewsArticle
import kotlinx.coroutines.flow.Flow

interface ArticleDetailRepository {

    suspend fun getArticleById(id: Long): NewsArticle?

    // ── Bookmark ──────────────────────────────────────────────────────────────

    fun observeIsBookmarked(id: Long): Flow<Boolean>
    suspend fun isBookmarked(id: Long): Boolean
    suspend fun toggleBookmark(articleId: Long): Boolean

    // ── Clap ──────────────────────────────────────────────────────────────────

    fun observeClapCount(id: Long): Flow<Int>
    suspend fun getClapCount(id: Long): Int
    suspend fun addClap(articleId: Long)

    // ── Read ──────────────────────────────────────────────────────────────────

    suspend fun markAsRead(articleId: Long)
}