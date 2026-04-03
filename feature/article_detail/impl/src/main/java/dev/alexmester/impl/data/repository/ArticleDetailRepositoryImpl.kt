package dev.alexmester.impl.data.repository

import dev.alexmester.impl.data.local.ArticleDetailLocalDataSource
import dev.alexmester.impl.data.mappers.toBookmarkEntity
import dev.alexmester.impl.data.mappers.toDomain
import dev.alexmester.impl.domain.repository.ArticleDetailRepository
import dev.alexmester.models.news.NewsArticle
import kotlinx.coroutines.flow.Flow

class ArticleDetailRepositoryImpl(
    private val local: ArticleDetailLocalDataSource,
) : ArticleDetailRepository {

    override suspend fun getArticleById(id: Long): NewsArticle? {
        val bookmark = local.getBookmarkById(id)
        if (bookmark != null) return bookmark.toDomain()

        val entity = local.getArticleById(id) ?: return null
        return entity.toDomain()
    }

    override suspend fun toggleBookmark(article: NewsArticle): Boolean {
        val isCurrentlyBookmarked = local.getBookmarkById(article.id) != null
        return if (isCurrentlyBookmarked) {
            local.deleteBookmark(article.id)
            false
        } else {
            local.insertBookmark(article.toBookmarkEntity())
            true
        }
    }

    override fun isBookmarked(id: Long): Flow<Boolean> = local.isBookmarked(id)

    override fun getClapCount(id: Long): Flow<Int> = local.getClapFlow(id)

    override suspend fun addClap(id: Long) = local.addClap(id)

    override suspend fun markAsRead(article: NewsArticle) =
        local.markAsRead(
            articleId = article.id,
            articleTitle = article.title,
        )

}