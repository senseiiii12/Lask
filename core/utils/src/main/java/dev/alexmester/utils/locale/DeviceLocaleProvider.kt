package dev.alexmester.utils.locale

import android.content.Context
import dev.alexmester.models.locale.SupportedLocales

class DeviceLocaleProvider(
    private val context: Context
) {
    fun getCountry(): String {
        val locale = context.resources.configuration.locales[0]
        val country = locale.country.lowercase()
        return if (country in SupportedLocales.SUPPORTED_COUNTRIES) country
        else SupportedLocales.FALLBACK_COUNTRY
    }

    fun getLanguage(): String {
        val locale = context.resources.configuration.locales[0]
        val language = locale.language.lowercase()
        return if (language in SupportedLocales.SUPPORTED_LANGUAGES) language
        else SupportedLocales.FALLBACK_LANGUAGE
    }
}