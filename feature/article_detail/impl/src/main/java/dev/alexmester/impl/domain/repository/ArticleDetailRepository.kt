package dev.alexmester.impl.domain.repository

import dev.alexmester.models.news.NewsArticle
import kotlinx.coroutines.flow.Flow

interface ArticleDetailRepository {
    suspend fun getArticleById(id: Long): NewsArticle?
    suspend fun toggleBookmark(article: NewsArticle): Boolean
    fun isBookmarked(id: Long): Flow<Boolean>
    fun getClapCount(id: Long): Flow<Int>
    suspend fun addClap(id: Long)
    suspend fun markAsRead(article: NewsArticle)
}