package dev.alexmester.impl.domain.repository

import dev.alexmester.models.news.NewsArticle
import kotlinx.coroutines.flow.Flow

interface BookmarksRepository {
    fun observeBookmarks(): Flow<List<NewsArticle>>
    suspend fun removeBookmarks(ids: Set<Long>)
}