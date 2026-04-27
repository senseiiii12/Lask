package dev.alexmester.impl.domain.usecase

import dev.alexmester.impl.domain.repository.ExploreRepository
import kotlinx.coroutines.flow.Flow

class ObserveReadArticleIdsExploreUseCase(
    private val repository: ExploreRepository
) {
    operator fun invoke(): Flow<List<Long>> =
        repository.observeReadArticleIds()
}