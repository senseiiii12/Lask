package dev.alexmester.datastore.model

/**
 * Пользовательские настройки приложения.
 *
 * [defaultCountry] — код страны по умолчанию для ленты (например "us", "de", "fr").
 * Используется в feature:news-feed как параметр source-country для GET /top-news.
 *
 * [defaultLanguage] — код языка новостей (например "en", "de", "fr").
 *
 * [isDarkTheme] — тёмная или светлая тема.
 * null означает "следовать системной теме".
 */
data class UserPreferences(
    val defaultCountry: String = "ru",
    val defaultLanguage: String = "ru",
    val isDarkTheme: Boolean? = null,
)