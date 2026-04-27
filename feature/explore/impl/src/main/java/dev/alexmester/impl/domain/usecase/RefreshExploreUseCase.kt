package dev.alexmester.impl.domain.usecase

import dev.alexmester.impl.domain.repository.ExploreRepository
import dev.alexmester.models.result.AppResult
import dev.alexmester.utils.constants.LaskConstants.PAGE_SIZE
import dev.alexmester.utils.extension.mutex.withTryLock
import kotlinx.coroutines.sync.Mutex

class RefreshExploreUseCase(
    private val repository: ExploreRepository,
    private val getQuery: GetInterestsExploreUseCase,
) {

    private val mutex = Mutex()

    suspend operator fun invoke(pageSize: Int = PAGE_SIZE): AppResult<Int> =
        mutex.withTryLock {
            val (query, language) = getQuery() ?: return AppResult.Success(0)

            repository.refresh(
                query = query,
                language = language,
                pageSize = pageSize
            )
        } ?: AppResult.Success(0)
}