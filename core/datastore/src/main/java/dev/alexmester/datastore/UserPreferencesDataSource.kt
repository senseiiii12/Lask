package dev.alexmester.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import dev.alexmester.datastore.model.UserPreferences
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
    }

    /**
     * Реактивный поток настроек — автоматически эмитит новое значение
     * при каждом изменении через update* методы.
     */
    val userPreferences: Flow<UserPreferences> = dataStore.data.map { prefs ->
        UserPreferences(
            defaultCountry = prefs[KEY_DEFAULT_COUNTRY] ?: "ru",
            defaultLanguage = prefs[KEY_DEFAULT_LANGUAGE] ?: "ru",
            isDarkTheme = if (prefs[KEY_IS_THEME_SET] == true) {
                prefs[KEY_IS_DARK_THEME]
            } else {
                null  // следовать системной теме
            },
        )
    }

    suspend fun updateDefaultCountry(country: String) {
        dataStore.edit { prefs ->
            prefs[KEY_DEFAULT_COUNTRY] = country
        }
    }

    suspend fun updateDefaultLanguage(language: String) {
        dataStore.edit { prefs ->
            prefs[KEY_DEFAULT_LANGUAGE] = language
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
}