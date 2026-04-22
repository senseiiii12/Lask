package dev.alexmester.impl.domain.repository

import dev.alexmester.models.news.NewsArticle
import kotlinx.coroutines.flow.Flow

interface ArticleDetailRepository {

    suspend fun getArticleById(id: Long): NewsArticle?

    fun observeIsBookmarked(id: Long): Flow<Boolean>

    suspend fun isBookmarked(id: Long): Boolean

    suspend fun toggleBookmark(articleId: Long): Boolean

    fun observeClapCount(id: Long): Flow<Int>

    suspend fun getClapCount(id: Long): Int

    suspend fun addClap(articleId: Long)

    suspend fun markAsRead(articleId: Long)

    suspend fun translateText(
        text: String,
        targetLanguage: String,
        sourceLanguage: String? = null,
    ): String

    suspend fun getAutoTranslateLanguage(): String
}