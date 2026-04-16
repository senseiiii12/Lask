package dev.alexmester.impl.presentation.locale_picker.mvi

sealed class LocalePickerSideEffect {
    data object NavigateBack : LocalePickerSideEffect()
    /** Сохранить язык И автоматически переключить страну. */
    data class ApplyWithCountryOverride(val country: String) : LocalePickerSideEffect()
    /** Сохранить страну И автоматически переключить язык. */
    data class ApplyWithLanguageOverride(val language: String) : LocalePickerSideEffect()
}