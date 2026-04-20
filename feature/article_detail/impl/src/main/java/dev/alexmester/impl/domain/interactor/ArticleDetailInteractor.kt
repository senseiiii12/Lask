package dev.alexmester.impl.domain.interactor

import dev.alexmester.impl.domain.repository.ArticleDetailRepository
import dev.alexmester.models.news.NewsArticle
import kotlinx.coroutines.flow.Flow

class ArticleDetailInteractor(
    private val repository: ArticleDetailRepository,
) {
    suspend fun getArticle(id: Long): NewsArticle? =
        repository.getArticleById(id)

    fun observeIsBookmarked(id: Long): Flow<Boolean> =
        repository.observeIsBookmarked(id)

    suspend fun isBookmarked(id: Long): Boolean =
        repository.isBookmarked(id)

    suspend fun toggleBookmark(articleId: Long): Boolean =
        repository.toggleBookmark(articleId)

    fun observeClapCount(id: Long): Flow<Int> =
        repository.observeClapCount(id)

    suspend fun getClapCount(id: Long): Int =
        repository.getClapCount(id)

    suspend fun addClap(articleId: Long) =
        repository.addClap(articleId)

    suspend fun markAsRead(articleId: Long) =
        repository.markAsRead(articleId)
}