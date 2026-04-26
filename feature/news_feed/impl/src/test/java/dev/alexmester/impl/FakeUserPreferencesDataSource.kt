package dev.alexmester.impl

import android.net.Uri
import dev.alexmester.datastore.UserPreferencesDataSource
import dev.alexmester.datastore.model.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeUserPreferencesDataSource : UserPreferencesDataSource {

    private val _prefs = MutableStateFlow(UserPreferences())

    override val userPreferences: Flow<UserPreferences> = _prefs.asStateFlow()

    fun emit(prefs: UserPreferences) {
        _prefs.value = prefs
    }

    override suspend fun completeOnboarding() = Unit
    override suspend fun initLocaleFromDevice(country: String, language: String) = Unit
    override suspend fun updateLocaleManually(country: String, language: String) = Unit
    override suspend fun updateAutoTranslateLanguage(languageCode: String) = Unit
    override suspend fun updateTheme(isDark: Boolean?) = Unit
    override suspend fun updateProfileName(name: String) = Unit
    override suspend fun updateAvatarUri(uri: Uri?) = Unit
    override suspend fun updateStreak(today: String) = Unit
    override suspend fun addInterest(keyword: String) = Unit
    override suspend fun removeInterest(keyword: String) = Unit
    override suspend fun addXp(xpDelta: Float) = Unit
}