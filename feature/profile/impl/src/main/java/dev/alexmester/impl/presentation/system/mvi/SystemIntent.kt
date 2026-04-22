package dev.alexmester.impl.presentation.system.mvi

sealed class SystemIntent {
    data class SetTheme(val theme: AppTheme) : SystemIntent()
    data object NavigateToLanguage : SystemIntent()
    data object NavigateToCountry : SystemIntent()
    data object NavigateToAutoTranslateLanguage : SystemIntent()
    data object Back : SystemIntent()
}

enum class AppTheme {
    SYSTEM, LIGHT, DARK;
}