package dev.alexmester.impl.presentation.profile.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.alexmester.impl.presentation.profile.mvi.ProfileIntent
import dev.alexmester.ui.R
import dev.alexmester.ui.components.buttons.LaskEditButton
import dev.alexmester.ui.desing_system.LaskColors
import dev.alexmester.ui.desing_system.LaskTheme
import dev.alexmester.ui.desing_system.LaskTypography

@Composable
fun ProfileNameRow(
    modifier: Modifier = Modifier,
    name: String,
    editName: String,
    levelData: Levels,
    isEdit: Boolean,
    onIntent: (ProfileIntent) -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.LaskColors.backgroundPrimary),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            AnimatedContent(targetState = isEdit) { isEdit ->
                if (isEdit){
                    Row(
                        modifier = modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        ProfileEditField(
                            modifier = Modifier.weight(1f),
                            isEdit = isEdit,
                            editName = editName,
                            onValueChange = { onIntent(ProfileIntent.OnProfileNameChange(it)) }
                        )
                        ButtonsInEditMode(
                            modifier = Modifier,
                            onApply = { onIntent(ProfileIntent.OnApplyEditChanges) },
                            onCancel = { onIntent(ProfileIntent.OnCancelInitMode) }
                        )
                    }
                }else{
                    Row(
                        modifier = modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            modifier = modifier.weight(1f, fill = false),
                            text = name,
                            style = MaterialTheme.LaskTypography.h5,
                            color = MaterialTheme.LaskColors.textPrimary,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1
                        )
                        LaskEditButton(
                            modifier = Modifier.size(30.dp),
                            tint = MaterialTheme.LaskColors.textLink,
                            onClick = { onIntent(ProfileIntent.OnInitEditMode) }
                        )
                    }
                }
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(levelData.iconRes),
                contentDescription = null,
                tint = Color.Unspecified
            )
            Text(
                text = levelData.title,
                style = MaterialTheme.LaskTypography.body1,
                color = MaterialTheme.LaskColors.textLink,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        }
    }
}

@Composable
fun ButtonsInEditMode(
    modifier: Modifier = Modifier,
    onApply: () -> Unit,
    onCancel: () -> Unit,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        IconButton(
            modifier = Modifier.size(30.dp),
            onClick = onApply
        ) {
            Icon(
                modifier = Modifier.size(25.dp),
                imageVector = ImageVector.vectorResource(R.drawable.ic_apply),
                contentDescription = null,
                tint = MaterialTheme.LaskColors.success
            )
        }
        IconButton(
            modifier = Modifier.size(30.dp),
            onClick = onCancel
        ) {
            Icon(
                modifier = Modifier.size(25.dp),
                imageVector = ImageVector.vectorResource(R.drawable.ic_cancel),
                contentDescription = null,
                tint = MaterialTheme.LaskColors.error
            )
        }
    }
}

enum class Levels(
    @param:DrawableRes val iconRes: Int,
    val title: String
) {
    LEVEL_1(R.drawable.ic_level_1, "Beginner"),
    LEVEL_2(R.drawable.ic_level_2, "Intermediate"),
    LEVEL_3(R.drawable.ic_level_3, "Advanced"),
    LEVEL_4(R.drawable.ic_level_4, "Pro"),
    LEVEL_5(R.drawable.ic_level_5, "Bookworm"),
}

@Preview(showBackground = true)
@Composable
private fun ProfileNameRowPreviewLight() {
    LaskTheme(darkTheme = false) {
        ProfileNameRow(
            modifier = Modifier,
            name = "Dianne Russell",
            editName = "Dianne",
            isEdit = true,
            levelData = Levels.LEVEL_5,
            onIntent = {}
        )
    }
}
@Preview(showBackground = true)
@Composable
private fun ProfileNameRowPreviewDark() {
    LaskTheme(darkTheme = true) {
        ProfileNameRow(
            modifier = Modifier,
            name = "Dianne Russell",
            editName = "123",
            isEdit = false,
            levelData = Levels.LEVEL_5,
            onIntent = {}
        )
    }
}