package dev.alexmester.impl.presentation.profile.mvi

import android.net.Uri
import dev.alexmester.impl.presentation.profile.components.Levels

data class ProfileState(
    val profileName: String = "Anonim",
    val editNameDraft: String = "",
    val avatarUri: Uri? = null,
    val editAvatarUriDraft: Uri? = null,
    val articleReadCount: Int = 0,
    val streakCount: Int = 0,
    val currentLevel: Int = 1,
    val currentXp: Float = 0f,
    val isEditingMode: Boolean = false,
) {
    val level: Levels
        get() = when {
            currentLevel >= 50 -> Levels.LEVEL_5
            currentLevel >= 20 -> Levels.LEVEL_4
            currentLevel >= 10 -> Levels.LEVEL_3
            currentLevel >= 5 -> Levels.LEVEL_2
            else -> Levels.LEVEL_1
        }

    companion object {

        fun mock(
            profileName: String = "Test User",
            avatarUri: Uri? = null,
            articleReadCount: Int = 42,
            streakCount: Int = 7,
            currentLevel: Int = 12,
            currentXp: Float = 0.6f,
            isEditingMode: Boolean = true,
            editNameDraft: String = profileName
        ) = ProfileState(
            profileName = profileName,
            avatarUri = avatarUri,
            articleReadCount = articleReadCount,
            streakCount = streakCount,
            currentLevel = currentLevel,
            currentXp = currentXp,
            isEditingMode = isEditingMode,
            editNameDraft = editNameDraft
        )
    }
}
