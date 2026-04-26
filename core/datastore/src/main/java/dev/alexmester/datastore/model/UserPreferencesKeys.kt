package dev.alexmester.datastore.model

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

internal object UserPreferencesKeys {
    val KEY_DEFAULT_COUNTRY = stringPreferencesKey("default_country")
    val KEY_DEFAULT_LANGUAGE = stringPreferencesKey("default_language")
    val KEY_AUTO_TRANSLATE_LANGUAGE = stringPreferencesKey("auto_translate_language")
    val KEY_IS_DARK_THEME = booleanPreferencesKey("is_dark_theme")
    val KEY_IS_THEME_SET = booleanPreferencesKey("is_theme_set")
    val KEY_ONBOARDING_DONE = booleanPreferencesKey("onboarding_completed")
    val KEY_LOCALE_MANUALLY_SET = booleanPreferencesKey("locale_manually_set")
    val KEY_AUTO_TRANSLATE_MANUALLY_SET = booleanPreferencesKey("auto_translate_manually_set")
    val KEY_PROFILE_NAME = stringPreferencesKey("profile_name")
    val KEY_AVATAR_URI = stringPreferencesKey("avatar_uri")
    val KEY_STREAK_COUNT = intPreferencesKey("streak_count")
    val KEY_LAST_STREAK_DATE = stringPreferencesKey("last_streak_date")
    val KEY_CURRENT_XP = floatPreferencesKey("current_xp")
    val KEY_CURRENT_LEVEL = intPreferencesKey("current_level")
    val KEY_INTERESTS = stringPreferencesKey("interests")
}