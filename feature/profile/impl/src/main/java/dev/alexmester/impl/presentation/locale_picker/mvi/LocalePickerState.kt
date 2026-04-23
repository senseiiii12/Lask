package dev.alexmester.impl.presentation.locale_picker.mvi

import dev.alexmester.api.navigation.LocalePickerType
import dev.alexmester.models.locale.LocaleItem
import dev.alexmester.ui.R
import dev.alexmester.ui.uitext.UiText
import dev.alexmester.utils.locale.CompatibilityWarning

data class LocalePickerState(
    val type: LocalePickerType,
    val items: List<LocaleItem> = emptyList(),
    val currentCode: String = "",
    val selectedCode: String = "",
    val compatibilityWarning: CompatibilityWarning? = null,
    val otherLocaleCode: String = "",
) {
    val title: UiText
        get() = when (type) {
            LocalePickerType.COUNTRY  -> UiText.StringResource(R.string.system_locale_country)
            LocalePickerType.LANGUAGE -> UiText.StringResource(R.string.system_locale_language)
            LocalePickerType.AUTO_TRANSLATE_LANGUAGE -> UiText.StringResource(R.string.system_locale_translate)
        }

    val isApplyEnabled: Boolean
        get() = selectedCode.isNotEmpty() && selectedCode != currentCode
}