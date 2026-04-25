package dev.alexmester.datastore

import android.net.Uri
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import dev.alexmester.datastore.model.UserPreferences
import dev.alexmester.datastore.model.UserPreferencesKeys.ANONIM
import dev.alexmester.datastore.model.UserPreferencesKeys.DELIMITER
import dev.alexmester.datastore.model.UserPreferencesKeys.KEY_AUTO_TRANSLATE_LANGUAGE
import dev.alexmester.datastore.model.UserPreferencesKeys.KEY_AUTO_TRANSLATE_MANUALLY_SET
import dev.alexmester.datastore.model.UserPreferencesKeys.KEY_AVATAR_URI
import dev.alexmester.datastore.model.UserPreferencesKeys.KEY_CURRENT_LEVEL
import dev.alexmester.datastore.model.UserPreferencesKeys.KEY_CURRENT_XP
import dev.alexmester.datastore.model.UserPreferencesKeys.KEY_DEFAULT_COUNTRY
import dev.alexmester.datastore.model.UserPreferencesKeys.KEY_DEFAULT_LANGUAGE
import dev.alexmester.datastore.model.UserPreferencesKeys.KEY_INTERESTS
import dev.alexmester.datastore.model.UserPreferencesKeys.KEY_IS_DARK_THEME
import dev.alexmester.datastore.model.UserPreferencesKeys.KEY_IS_THEME_SET
import dev.alexmester.datastore.model.UserPreferencesKeys.KEY_LAST_STREAK_DATE
import dev.alexmester.datastore.model.UserPreferencesKeys.KEY_LOCALE_MANUALLY_SET
import dev.alexmester.datastore.model.UserPreferencesKeys.KEY_ONBOARDING_DONE
import dev.alexmester.datastore.model.UserPreferencesKeys.KEY_PROFILE_NAME
import dev.alexmester.datastore.model.UserPreferencesKeys.KEY_STREAK_COUNT
import dev.alexmester.models.locale.SupportedLocales
import dev.alexmester.utils.date.DateUtils.isYesterday
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class UserPreferencesDataSource(
    private val dataStore: DataStore<Preferences>,
) {

    val userPreferences: Flow<UserPreferences> = dataStore.data.map { prefs ->
        UserPreferences(
            defaultCountry = prefs[KEY_DEFAULT_COUNTRY] ?: SupportedLocales.FALLBACK_COUNTRY,
            defaultLanguage = prefs[KEY_DEFAULT_LANGUAGE] ?: SupportedLocales.FALLBACK_LANGUAGE,
            isDarkTheme = if (prefs[KEY_IS_THEME_SET] == true) prefs[KEY_IS_DARK_THEME] else null,
            isOnboardingCompleted = prefs[KEY_ONBOARDING_DONE] ?: false,
            isLocaleManuallySet = prefs[KEY_LOCALE_MANUALLY_SET] ?: false,
            profileName = prefs[KEY_PROFILE_NAME] ?: ANONIM,
            avatarUri = prefs[KEY_AVATAR_URI],
            streakCount = prefs[KEY_STREAK_COUNT] ?: 0,
            lastStreakDate = prefs[KEY_LAST_STREAK_DATE],
            currentXp = prefs[KEY_CURRENT_XP] ?: 0f,
            currentLevel = prefs[KEY_CURRENT_LEVEL] ?: 1,
            interests = prefs[KEY_INTERESTS]
                ?.split(DELIMITER)
                ?.filter { it.isNotBlank() }
                ?: emptyList(),
            autoTranslateLanguage = prefs[KEY_AUTO_TRANSLATE_LANGUAGE]
                ?: SupportedLocales.FALLBACK_LANGUAGE,
            isAutoTranslateManuallySet = prefs[KEY_AUTO_TRANSLATE_MANUALLY_SET] ?: false,
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
            if (prefs[KEY_AUTO_TRANSLATE_MANUALLY_SET] != true) {
                prefs[KEY_AUTO_TRANSLATE_LANGUAGE] = language
            }
        }
    }

    suspend fun updateLocaleManually(country: String, language: String) {
        dataStore.edit { prefs ->
            prefs[KEY_DEFAULT_COUNTRY] = country
            prefs[KEY_DEFAULT_LANGUAGE] = language
            prefs[KEY_LOCALE_MANUALLY_SET] = true
        }
    }

    suspend fun updateAutoTranslateLanguage(languageCode: String) {
        dataStore.edit { prefs ->
            prefs[KEY_AUTO_TRANSLATE_LANGUAGE] = languageCode
            prefs[KEY_AUTO_TRANSLATE_MANUALLY_SET] = true
        }
    }


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
        dataStore.edit { it[KEY_PROFILE_NAME] = name.trim().ifEmpty { ANONIM } }
    }

    suspend fun updateAvatarUri(uri: Uri?) {
        dataStore.edit { prefs ->
            if (uri == null) prefs.remove(KEY_AVATAR_URI)
            else prefs[KEY_AVATAR_URI] = uri.toString()
        }
    }

    suspend fun updateStreak(today: String) {
        dataStore.edit { prefs ->
            val last = prefs[KEY_LAST_STREAK_DATE]
            val current = prefs[KEY_STREAK_COUNT] ?: 0

            val newStreak = when {
                last == null -> 1
                last == today -> current
                isYesterday(last, today) -> current + 1
                else -> 1
            }

            prefs[KEY_STREAK_COUNT] = newStreak
            prefs[KEY_LAST_STREAK_DATE] = today
        }
    }

    suspend fun addInterest(keyword: String) {
        dataStore.edit { prefs ->
            val current = prefs[KEY_INTERESTS]
                ?.split(DELIMITER)
                ?.filter { it.isNotBlank() }
                ?.toMutableList()
                ?: mutableListOf()
            if (keyword.trim().isNotBlank() && keyword.trim() !in current) {
                current.add(keyword.trim())
                prefs[KEY_INTERESTS] = current.joinToString(DELIMITER)
            }
        }
    }

    suspend fun removeInterest(keyword: String) {
        dataStore.edit { prefs ->
            val current = prefs[KEY_INTERESTS]
                ?.split(DELIMITER)
                ?.filter { it.isNotBlank() && it != keyword }
                ?: emptyList()
            prefs[KEY_INTERESTS] = current.joinToString(DELIMITER)
        }
    }

    suspend fun addXp(xpDelta: Float) {
        dataStore.edit { prefs ->
            var xp = (prefs[KEY_CURRENT_XP] ?: 0f) + xpDelta
            var level = prefs[KEY_CURRENT_LEVEL] ?: 1

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

}