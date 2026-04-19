package dev.alexmester.impl.domain.usecase

import dev.alexmester.datastore.UserPreferencesDataSource
import dev.alexmester.impl.domain.repository.NewsFeedRepository
import dev.alexmester.models.result.AppResult
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class RefreshFeedUseCase(
    private val repository: NewsFeedRepository,
    private val preferencesDataSource: UserPreferencesDataSource,
) {
    private val mutex = Mutex()

    suspend operator fun invoke(): AppResult<Int> {
        if (mutex.isLocked) return AppResult.Success(0)
        return mutex.withLock {
            val prefs = preferencesDataSource.userPreferences.first()
            repository.refreshFeed(
                country = prefs.defaultCountry,
                language = prefs.defaultLanguage,
            )
        }
    }
}