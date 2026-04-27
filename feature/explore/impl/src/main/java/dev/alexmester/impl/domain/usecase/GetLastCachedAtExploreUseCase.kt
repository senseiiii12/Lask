package dev.alexmester.impl.domain.usecase

import dev.alexmester.impl.domain.repository.ExploreRepository

class GetLastCachedAtExploreUseCase(
    private val repository: ExploreRepository
) {
    suspend operator fun invoke(): Long? =
        repository.getLastCachedAt()
}