package dev.alexmester.impl.domain.interactor

import dev.alexmester.impl.domain.repository.ArticleDetailRepository
import dev.alexmester.models.news.NewsArticle
import kotlinx.coroutines.flow.Flow

class ArticleDetailInteractor(
    private val repository: ArticleDetailRepository,
) {
    suspend fun getArticle(id: Long): NewsArticle? = repository.getArticleById(id)

    suspend fun toggleBookmark(article: NewsArticle): Boolean = repository.toggleBookmark(article)

    fun isBookmarked(id: Long): Flow<Boolean> = repository.isBookmarked(id)

    fun getClapCount(id: Long): Flow<Int> = repository.getClapCount(id)

    suspend fun addClap(id: Long) = repository.addClap(id)

    suspend fun markAsRead(article: NewsArticle) = repository.markAsRead(article)
}