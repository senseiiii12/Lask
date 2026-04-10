package dev.alexmester.impl.presentation.profile.components

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.alexmester.impl.presentation.profile.mvi.ProfileIntent
import dev.alexmester.ui.desing_system.LaskTheme

@Composable
fun ProfileTopHeader(
    modifier: Modifier = Modifier,
    avatarUri: Uri?,
    editAvatarUri: Uri?,
    name: String,
    editName: String,
    currentLevel: Levels,
    isEdit: Boolean,
    onIntent: (ProfileIntent) -> Unit,
) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    )  { uri: Uri? ->
        uri?.let {
            context.contentResolver.takePersistableUriPermission(
                it,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            onIntent(ProfileIntent.OnProfileAvatarChange(it))
        }
    }

    val displayUri = editAvatarUri ?: avatarUri

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        ProfileAvatar(
            modifier = Modifier,
            displayUri = displayUri,
            name = name,
            isEdit = isEdit,
            onClick = { launcher.launch("image/*") }
        )
        ProfileNameRow(
            modifier = Modifier,
            name = name,
            levelData = currentLevel,
            isEdit = isEdit,
            onIntent = { onIntent(it) },
            editName = editName
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ProfileTopHeaderPreviewDark() {
    LaskTheme(darkTheme = true) {
        ProfileTopHeader(
            avatarUri = null,
            editAvatarUri = null,
            name = "Dianne",
            editName = "Dia",
            currentLevel = Levels.LEVEL_5,
            isEdit = true,
            onIntent = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ProfileTopHeaderPreviewLight() {
    LaskTheme(darkTheme = false) {
        ProfileTopHeader(
            avatarUri = null,
            editAvatarUri = null,
            name = "Dianne",
            editName = "Dianne",
            currentLevel = Levels.LEVEL_5,
            isEdit = false,
            onIntent = {}
        )
    }
}