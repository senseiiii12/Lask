package dev.alexmester.impl.domain.usecase

import dev.alexmester.datastore.UserPreferencesDataSource
import kotlinx.coroutines.flow.first

class GetCurrentLocaleUseCase(
    private val preferencesDataSource: UserPreferencesDataSource,
) {
    suspend operator fun invoke(): Pair<String, String> {
        val prefs = preferencesDataSource.userPreferences.first()
        return prefs.defaultCountry to prefs.defaultLanguage
    }
}