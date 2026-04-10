package dev.alexmester.impl.presentation.profile.mvi

import android.net.Uri

sealed class ProfileIntent {
    data object OnInitEditMode: ProfileIntent()
    data object OnApplyEditChanges: ProfileIntent()
    data object OnCancelInitMode: ProfileIntent()
    data class OnProfileNameChange(val value: String): ProfileIntent()
    data class OnProfileAvatarChange(val uri: Uri?): ProfileIntent()
    data object NavigateToReadArticles : ProfileIntent()
    data object NavigateToClappedArticles : ProfileIntent()
}