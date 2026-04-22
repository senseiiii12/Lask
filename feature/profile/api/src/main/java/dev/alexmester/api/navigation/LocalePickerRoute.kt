package dev.alexmester.api.navigation

import kotlinx.serialization.Serializable

@Serializable
data class LocalePickerRoute(
    val type: LocalePickerType,
)

enum class LocalePickerType {
    COUNTRY,
    LANGUAGE,
    AUTO_TRANSLATE_LANGUAGE,
}