package dev.alexmester.impl.presentation.system.mvi

import dev.alexmester.utils.BuildLocale

data class SystemState(
    val theme: AppTheme = AppTheme.SYSTEM,
    val languageCode: String = "en",
    val countryCode: String = "us",
    val autoTranslateLanguage: String = "en",
) {

    val autoTranslateDisplayName: String
        get() = autoTranslateLanguage?.let {
            BuildLocale.languageCodeToFullLanguageName(it)
        } ?: "Off"

    val languageDisplayName: String
        get() = BuildLocale.languageCodeToFullLanguageName(languageCode)

    val countryDisplayName: String
        get() = BuildLocale.countryCodeToFullCountryName(countryCode)
}