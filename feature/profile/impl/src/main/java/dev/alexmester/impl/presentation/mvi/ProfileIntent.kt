package dev.alexmester.impl.presentation.mvi

sealed class ProfileIntent {
    data object OnInitEditMode: ProfileIntent()
    data object OnApplyEditChanges: ProfileIntent()
    data object OnCancelInitMode: ProfileIntent()
    data class OnProfileNameChange(val value: String): ProfileIntent()
    data class OnProfileAvatarChange(val value: String?): ProfileIntent()
    data object NavigateToReadArticles : ProfileIntent()
    data object NavigateToClappedArticles : ProfileIntent()
}