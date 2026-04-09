package dev.alexmester.impl.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.alexmester.impl.presentation.mvi.ProfileIntent
import dev.alexmester.impl.presentation.mvi.ProfileState
import dev.alexmester.ui.components.menu.LaskRowMenu
import dev.alexmester.ui.desing_system.LaskColors
import dev.alexmester.ui.desing_system.LaskTheme
import dev.alexmester.ui.desing_system.LaskTypography

@Composable
fun ProfileContent(
    modifier: Modifier = Modifier,
    profileState: ProfileState,
    onIntent: (ProfileIntent) -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.LaskColors.backgroundPrimary)
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        ProfileTopHeader(
            avatarUri = profileState.avatarUri,
            editAvatarUri = profileState.editAvatarUriDraft,
            name = profileState.profileName,
            editName = profileState.editNameDraft,
            currentLevel = profileState.level,
            isEdit = profileState.isEditingMode,
            onIntent = { onIntent(it) }
        )
        Spacer(modifier = Modifier.height(24.dp))
        ProfileStatisticRow(
            modifier = Modifier,
            articleReadCount = profileState.articleReadCount,
            streakCount = profileState.streakCount,
            levelCount = profileState.currentLevel
        )
        Spacer(modifier = Modifier.height(24.dp))
        Spacer(
            modifier = Modifier
                .height(1.dp)
                .fillMaxWidth()
                .background(MaterialTheme.LaskColors.textSecondary)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Reading History",
            style = MaterialTheme.LaskTypography.h5,
            color = MaterialTheme.LaskColors.textPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            LaskRowMenu(
                modifier = Modifier,
                menuName = "Clapped Articles",
                onClick = { onIntent(ProfileIntent.NavigateToClappedArticles) }
            )
            LaskRowMenu(
                modifier = Modifier,
                menuName = "Read Articles",
                onClick = { onIntent(ProfileIntent.NavigateToReadArticles) }
            )
        }
    }
}

@Preview
@Composable
private fun ProfileContentPreviewDark() {
    LaskTheme(darkTheme = true) {
        ProfileContent(
            modifier = Modifier,
            profileState = ProfileState.mock(),
            onIntent = {}
        )
    }
}
@Preview
@Composable
private fun ProfileContentPreviewLight() {
    LaskTheme(darkTheme = false) {
        ProfileContent(
            modifier = Modifier,
            profileState = ProfileState.mock()
                .copy(isEditingMode = false),
            onIntent = {}
        )
    }
}