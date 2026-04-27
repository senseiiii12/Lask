package dev.alexmester.impl.domain.usecase

import dev.alexmester.datastore.UserPreferencesDataSource
import dev.alexmester.utils.constants.LaskConstants
import kotlinx.coroutines.flow.first

class GetInterestsExploreUseCase(
    private val preferencesDataSource: UserPreferencesDataSource,
) {
    suspend operator fun invoke(): Pair<String, String>? {
        val prefs = preferencesDataSource.userPreferences.first()

        val query = prefs.interests
            .asSequence()
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .joinToString(separator = LaskConstants.SEPARATOR_OR)
            .takeIf { it.isNotBlank() }
            ?: return null

        return query to prefs.defaultLanguage
    }
}