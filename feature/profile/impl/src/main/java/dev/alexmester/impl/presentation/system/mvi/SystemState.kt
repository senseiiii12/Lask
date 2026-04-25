package dev.alexmester.impl.presentation.system.mvi

import dev.alexmester.utils.locale.LocaleUtils

data class SystemState(
    val theme: AppTheme = AppTheme.SYSTEM,
    val languageCode: String = "en",
    val countryCode: String = "us",
    val autoTranslateLanguage: String = "en",
) {

    val autoTranslateDisplayName: String
        get() = autoTranslateLanguage?.let {
            LocaleUtils.languageCodeToFullLanguageName(it)
        } ?: "Off"

    val languageDisplayName: String
        get() = LocaleUtils.languageCodeToFullLanguageName(languageCode)

    val countryDisplayName: String
        get() = LocaleUtils.countryCodeToFullCountryName(countryCode)
}