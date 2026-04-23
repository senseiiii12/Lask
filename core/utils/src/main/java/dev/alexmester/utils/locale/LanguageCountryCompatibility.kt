package dev.alexmester.utils.locale


val LANGUAGE_TO_COUNTRIES: Map<String, List<String>> = mapOf(
    "en" to listOf("us", "gb", "au", "ca", "nz", "ie", "za", "in", "sg", "ph"),
    "de" to listOf("de", "at", "ch"),
    "fr" to listOf("fr", "be", "ch", "ca"),
    "es" to listOf("es", "mx", "ar"),
    "pt" to listOf("pt", "br"),
    "it" to listOf("it"),
    "ru" to listOf("ru", "ua"),
    "zh" to listOf("cn", "tw", "hk", "sg"),
    "ja" to listOf("jp"),
    "ko" to listOf("kr"),
    "ar" to listOf("sa", "ae", "eg"),
    "nl" to listOf("nl", "be"),
    "pl" to listOf("pl"),
    "sv" to listOf("se"),
    "no" to listOf("no"),
    "da" to listOf("dk"),
    "fi" to listOf("fi"),
    "cs" to listOf("cz"),
    "ro" to listOf("ro"),
    "hu" to listOf("hu"),
    "tr" to listOf("tr"),
    "he" to listOf("il"),
    "uk" to listOf("ua"),
    "id" to listOf("id"),
    "ms" to listOf("my"),
    "th" to listOf("th"),
    "vi" to listOf("vn"),
    "fa" to listOf("ae"),
)

/** Обратный маппинг: страна → список языков. */
val COUNTRY_TO_LANGUAGES: Map<String, List<String>> by lazy {
    buildMap<String, MutableList<String>> {
        LANGUAGE_TO_COUNTRIES.forEach { (lang, countries) ->
            countries.forEach { country ->
                getOrPut(country) { mutableListOf() }.add(lang)
            }
        }
    }
}

data class CompatibilityWarning(
    val suggestedCountry: String,
    val suggestedLanguage: String,
)

/**
 * null — совместимо, иначе — варианты исправления.
 */
fun checkCompatibility(language: String, country: String): CompatibilityWarning? {
    val compatibleCountries = LANGUAGE_TO_COUNTRIES[language] ?: return null
    if (country in compatibleCountries) return null

    val compatibleLanguages = COUNTRY_TO_LANGUAGES[country] ?: return null

    return CompatibilityWarning(
        suggestedCountry = compatibleCountries.first(),
        suggestedLanguage = compatibleLanguages.first(),
    )
}