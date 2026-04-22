package dev.alexmester.datastore.model

import dev.alexmester.models.locale.SupportedLocales

data class UserPreferences(
    val defaultCountry: String = SupportedLocales.FALLBACK_COUNTRY,
    val defaultLanguage: String = SupportedLocales.FALLBACK_LANGUAGE,
    val isDarkTheme: Boolean? = null,
    val isOnboardingCompleted: Boolean = false,
    val isLocaleManuallySet: Boolean = false,
    val profileName: String = "Anonim",
    val avatarUri: String? = null,
    val streakCount: Int = 0,
    val lastStreakDate: String? = null,
    val currentXp: Float = 0f,
    val currentLevel: Int = 1,
    val interests: List<String> = emptyList(),
    val autoTranslateLanguage: String? = null,
)