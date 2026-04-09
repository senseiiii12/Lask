package dev.alexmester.impl.presentation.components

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import dev.alexmester.impl.presentation.mvi.ProfileIntent
import dev.alexmester.ui.components.avatars.AuthorAvatar
import dev.alexmester.ui.desing_system.LaskColors
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
        Box(
            modifier = modifier
                .size(120.dp)
                .then(
                    if (isEdit) Modifier.border(
                        width = 2.dp,
                        color = MaterialTheme.LaskColors.textLink,
                        shape = CircleShape,
                    ) else Modifier
                )
                .clip(CircleShape)
                .clickable(enabled = isEdit) { launcher.launch("image/*") },
            contentAlignment = Alignment.Center,
        ) {
            if (isEdit) {
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .zIndex(1f),
                ) {
                    Icon(
                        imageVector = Icons.Default.AddPhotoAlternate,
                        contentDescription = null,
                        tint = MaterialTheme.LaskColors.textLink,
                    )
                }
            }
            if (displayUri != null) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(displayUri)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape),
                )
            } else {
                AuthorAvatar(
                    modifier = Modifier.size(120.dp),
                    char = name.firstOrNull()?.uppercase() ?: "A",
                )
            }
        }

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