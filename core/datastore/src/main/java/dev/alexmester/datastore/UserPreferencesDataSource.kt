package dev.alexmester.datastore

import android.net.Uri
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import dev.alexmester.datastore.model.UserPreferences
import dev.alexmester.models.news.SupportedLocales
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreferencesDataSource(
    private val dataStore: DataStore<Preferences>,
) {

    companion object {
        private val KEY_DEFAULT_COUNTRY = stringPreferencesKey("default_country")
        private val KEY_DEFAULT_LANGUAGE = stringPreferencesKey("default_language")
        private val KEY_IS_DARK_THEME = booleanPreferencesKey("is_dark_theme")
        private val KEY_IS_THEME_SET = booleanPreferencesKey("is_theme_set")
        private val KEY_ONBOARDING_DONE = booleanPreferencesKey("onboarding_completed")
        private val KEY_LOCALE_MANUALLY_SET = booleanPreferencesKey("locale_manually_set")
        // Profile
        private val KEY_PROFILE_NAME = stringPreferencesKey("profile_name")
        private val KEY_AVATAR_URI = stringPreferencesKey("avatar_uri")
        private val KEY_STREAK_COUNT = intPreferencesKey("streak_count")
        private val KEY_LAST_STREAK_DATE = stringPreferencesKey("last_streak_date")
        private val KEY_CURRENT_XP = floatPreferencesKey("current_xp")
        private val KEY_CURRENT_LEVEL = intPreferencesKey("current_level")
    }

    val userPreferences: Flow<UserPreferences> = dataStore.data.map { prefs ->
        UserPreferences(
            defaultCountry = prefs[KEY_DEFAULT_COUNTRY] ?: SupportedLocales.FALLBACK_COUNTRY,
            defaultLanguage = prefs[KEY_DEFAULT_LANGUAGE] ?: SupportedLocales.FALLBACK_LANGUAGE,
            isDarkTheme = if (prefs[KEY_IS_THEME_SET] == true) prefs[KEY_IS_DARK_THEME] else null,
            isOnboardingCompleted = prefs[KEY_ONBOARDING_DONE] ?: false,
            isLocaleManuallySet = prefs[KEY_LOCALE_MANUALLY_SET] ?: false,
            profileName = prefs[KEY_PROFILE_NAME] ?: "Anonim",
            avatarUri = prefs[KEY_AVATAR_URI],
            streakCount = prefs[KEY_STREAK_COUNT] ?: 0,
            lastStreakDate = prefs[KEY_LAST_STREAK_DATE],
            currentXp = prefs[KEY_CURRENT_XP] ?: 0f,
            currentLevel = prefs[KEY_CURRENT_LEVEL] ?: 1,
        )
    }

    suspend fun completeOnboarding() {
        dataStore.edit { it[KEY_ONBOARDING_DONE] = true }
    }

    suspend fun initLocaleFromDevice(country: String, language: String) {
        dataStore.edit { prefs ->
            prefs[KEY_DEFAULT_COUNTRY] = country
            prefs[KEY_DEFAULT_LANGUAGE] = language
            prefs[KEY_LOCALE_MANUALLY_SET] = false
        }
    }

    suspend fun updateLocaleManually(country: String, language: String) {
        dataStore.edit { prefs ->
            prefs[KEY_DEFAULT_COUNTRY] = country
            prefs[KEY_DEFAULT_LANGUAGE] = language
            prefs[KEY_LOCALE_MANUALLY_SET] = true
        }
    }

    /**
     * [isDark] null — сбросить на системную тему.
     */
    suspend fun updateTheme(isDark: Boolean?) {
        dataStore.edit { prefs ->
            if (isDark == null) {
                prefs.remove(KEY_IS_DARK_THEME)
                prefs[KEY_IS_THEME_SET] = false
            } else {
                prefs[KEY_IS_DARK_THEME] = isDark
                prefs[KEY_IS_THEME_SET] = true
            }
        }
    }
    // ── Profile ───────────────────────────────────────────────────────────────

    suspend fun updateProfileName(name: String) {
        dataStore.edit { it[KEY_PROFILE_NAME] = name.trim().ifEmpty { "Anonim" } }
    }

    suspend fun updateAvatarUri(uri: Uri?) {
        dataStore.edit { prefs ->
            if (uri == null) prefs.remove(KEY_AVATAR_URI)
            else prefs[KEY_AVATAR_URI] = uri.toString()
        }
    }

    /**
     * Вызывается при каждом открытии приложения.
     * today — ISO date string "yyyy-MM-dd"
     */
    suspend fun updateStreak(today: String) {
        dataStore.edit { prefs ->
            val last = prefs[KEY_LAST_STREAK_DATE]
            val current = prefs[KEY_STREAK_COUNT] ?: 0

            val newStreak = when {
                last == null -> 1
                last == today -> current           // уже заходили сегодня
                isYesterday(last, today) -> current + 1
                else -> 1                          // пропустили день
            }

            prefs[KEY_STREAK_COUNT] = newStreak
            prefs[KEY_LAST_STREAK_DATE] = today
        }
    }

    /**
     * Добавляем XP и при необходимости повышаем уровень.
     * XP_for_level_N = 10 * N^1.8
     */
    suspend fun addXp(xpDelta: Float) {
        dataStore.edit { prefs ->
            var xp = (prefs[KEY_CURRENT_XP] ?: 0f) + xpDelta
            var level = prefs[KEY_CURRENT_LEVEL] ?: 1

            // Повышаем уровень пока хватает XP
            while (true) {
                val needed = xpForLevel(level)
                if (xp >= needed) {
                    xp -= needed
                    level++
                } else break
            }

            prefs[KEY_CURRENT_XP] = xp
            prefs[KEY_CURRENT_LEVEL] = level
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun xpForLevel(level: Int): Float =
        (10.0 * Math.pow(level.toDouble(), 1.8)).toFloat()

    /**
     * Проверяем что lastDate — вчерашний день относительно today.
     * Формат: "yyyy-MM-dd"
     */
    private fun isYesterday(lastDate: String, today: String): Boolean {
        return try {
            val last = java.time.LocalDate.parse(lastDate)
            val todayDate = java.time.LocalDate.parse(today)
            last.plusDays(1) == todayDate
        } catch (e: Exception) {
            false
        }
    }
}