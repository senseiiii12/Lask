package dev.alexmester.impl.domain.interactor

import dev.alexmester.impl.domain.repository.BookmarksRepository
import dev.alexmester.models.news.NewsArticle
import kotlinx.coroutines.flow.Flow

class BookmarksInteractor(
    private val repository: BookmarksRepository,
) {
    fun observeBookmarks(): Flow<List<NewsArticle>> =
        repository.observeBookmarks()

    suspend fun removeBookmarks(ids: Set<Long>) =
        repository.removeBookmarks(ids)
}