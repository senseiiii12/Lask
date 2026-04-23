package dev.alexmester.utils.locale

import dev.alexmester.models.locale.LanguageFlagMap

fun countryCodeToFlagEmoji(code: String): String {
    return code
        .uppercase()
        .map { char -> Character.toCodePoint('\uD83C', '\uDDE6' + (char - 'A')) }
        .joinToString("") { String(Character.toChars(it)) }
}

fun languageCodeToFlagEmoji(code: String): String{
    return countryCodeToFlagEmoji(LanguageFlagMap.flagCountryFor(code))
}