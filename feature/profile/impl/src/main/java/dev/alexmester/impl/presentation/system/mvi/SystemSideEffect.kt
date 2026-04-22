package dev.alexmester.impl.presentation.system.mvi

import dev.alexmester.api.navigation.LocalePickerType

sealed class SystemSideEffect {
    data object NavigateBack : SystemSideEffect()
    data class NavigateToLocalePicker(val type: LocalePickerType) : SystemSideEffect()
    data object NavigateToAutoTranslatePicker : SystemSideEffect()
}