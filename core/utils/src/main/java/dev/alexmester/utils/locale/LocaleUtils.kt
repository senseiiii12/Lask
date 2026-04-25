package dev.alexmester.utils.locale

import dev.alexmester.models.locale.LanguageFlagMap
import dev.alexmester.models.locale.LocaleItem
import dev.alexmester.models.locale.SupportedLocales
import java.util.Locale

object LocaleUtils {

    fun countryCodeToFlagEmoji(code: String): String {
        return code
            .uppercase()
            .map { char -> Character.toCodePoint('\uD83C', '\uDDE6' + (char - 'A')) }
            .joinToString("") { String(Character.toChars(it)) }
    }

    fun languageCodeToFlagEmoji(code: String): String{
        return countryCodeToFlagEmoji(LanguageFlagMap.flagCountryFor(code))
    }

    fun languageCodeToFullLanguageName(languageCode: String): String =
        Locale(languageCode.uppercase()).getDisplayLanguage(Locale.ENGLISH)
            .replaceFirstChar { it.uppercase() }

    fun countryCodeToFullCountryName(countryCode: String): String =
        Locale("", countryCode.uppercase()).getDisplayCountry(Locale.ENGLISH)
            .replaceFirstChar { it.uppercase() }

    fun buildCountryItems(): List<LocaleItem> =
        SupportedLocales.SUPPORTED_COUNTRIES
            .map { code ->
                LocaleItem(
                    code = code,
                    displayName = countryCodeToFullCountryName(code),
                    flag = countryCodeToFlagEmoji(code),
                )
            }
            .sortedBy { it.displayName }

    fun buildLanguageItems(): List<LocaleItem> =
        SupportedLocales.SUPPORTED_LANGUAGES
            .map { code ->
                LocaleItem(
                    code = code,
                    displayName = languageCodeToFullLanguageName(code),
                    flag = languageCodeToFlagEmoji(code),
                )
            }
            .sortedBy { it.displayName }
}