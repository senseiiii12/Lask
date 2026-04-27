package dev.alexmester.impl.domain.usecase

import dev.alexmester.impl.domain.repository.ExploreRepository
import dev.alexmester.models.news.NewsArticle
import kotlinx.coroutines.flow.Flow

class ObserveArticlesExploreUseCase(
    private val repository: ExploreRepository
) {
    operator fun invoke(): Flow<List<NewsArticle>> =
        repository.observeArticles()
}