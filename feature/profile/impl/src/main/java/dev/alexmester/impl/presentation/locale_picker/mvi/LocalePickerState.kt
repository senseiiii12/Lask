package dev.alexmester.impl.presentation.locale_picker.mvi

import dev.alexmester.api.navigation.LocalePickerType
import dev.alexmester.models.locale.LocaleItem
import dev.alexmester.ui.R
import dev.alexmester.ui.uitext.UiText
import dev.alexmester.utils.CompatibilityWarning

data class LocalePickerState(
    val type: LocalePickerType,
    val items: List<LocaleItem> = emptyList(),
    val selectedCode: String = "",
    val pendingCode: String = "",
    val compatibilityWarning: CompatibilityWarning? = null,
    val otherLocaleCode: String = "",
) {
    val title: UiText
        get() = when (type) {
            LocalePickerType.COUNTRY  -> UiText.StringResource(R.string.locale_title_country)
            LocalePickerType.LANGUAGE -> UiText.StringResource(R.string.locale_title_language)
        }

    val isApplyEnabled: Boolean
        get() = pendingCode.isNotEmpty() && pendingCode != selectedCode
}