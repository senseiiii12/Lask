package dev.alexmester.impl.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import dev.alexmester.impl.presentation.mvi.BookmarksIntent
import dev.alexmester.ui.R
import dev.alexmester.ui.components.buttons.LaskTextButton
import dev.alexmester.ui.components.buttons.LaskEditButton
import dev.alexmester.ui.desing_system.LaskColors
import dev.alexmester.ui.desing_system.LaskTypography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun BookmarksTopBar(
    isEditMode: Boolean,
    onIntent: (BookmarksIntent) -> Unit,
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.LaskColors.brand_blue10
        ),
        title = {
            Text(
                text = stringResource(R.string.tab_bookmark),
                style = MaterialTheme.LaskTypography.h4,
                color = MaterialTheme.LaskColors.textPrimary,
                overflow = TextOverflow.Ellipsis
            )
        },
        actions = {
            AnimatedContent(targetState = isEditMode) { isEdit ->
                if (isEdit) {
                    Row {
                        LaskTextButton(
                            text = stringResource(R.string.bookmarks_cancel),
                            textColor = MaterialTheme.LaskColors.error,
                            onClick = { onIntent(BookmarksIntent.CancelDeletion) },
                        )
                        LaskTextButton(
                            text = stringResource(R.string.bookmarks_done),
                            textColor = MaterialTheme.LaskColors.brand_blue,
                            onClick = { onIntent(BookmarksIntent.ConfirmDeletion) },
                        )
                    }
                } else {
                    LaskEditButton(
                        tint = MaterialTheme.LaskColors.textPrimary,
                        onClick = { onIntent(BookmarksIntent.ToggleEditMode) }
                    )
                }
            }
        }
    )
}