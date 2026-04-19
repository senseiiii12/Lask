package dev.alexmester.impl.domain.usecase

import dev.alexmester.impl.domain.repository.NewsFeedRepository

class GetLastCachedAtUseCase(
    private val repository: NewsFeedRepository,
) {
    suspend operator fun invoke(): Long? =
        repository.getLastCachedAt()
}