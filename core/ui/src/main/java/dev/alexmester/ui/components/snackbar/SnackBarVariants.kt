package dev.alexmester.ui.components.snackbar

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkAdded
import androidx.compose.material.icons.filled.BookmarkRemove
import androidx.compose.material.icons.filled.Interests
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import com.snackbar.snackswipe.SnackSwipeController
import com.snackbar.snackswipe.showSnackSwipe
import dev.alexmester.ui.desing_system.LaskColors
import dev.alexmester.ui.desing_system.LaskTypography

fun SnackSwipeController.showBookmarkSnackbar(
    isBookmarked: Boolean,
    backgroundColor: Color,
    addedText: String,
    removedText: String,
) {
    showSnackSwipe(
        backgroundColor = backgroundColor,
        messageText = {
            Text(
                text = if (isBookmarked) addedText else removedText,
                color = MaterialTheme.LaskColors.textPrimary,
                style = MaterialTheme.LaskTypography.body2,
            )
        },
        icon = {
            Icon(
                imageVector = if (isBookmarked) Icons.Default.BookmarkAdded else Icons.Default.BookmarkRemove,
                contentDescription = null,
                tint = if (isBookmarked) MaterialTheme.LaskColors.success else MaterialTheme.LaskColors.error,
            )
        },
    )
}

fun SnackSwipeController.showWarningSnackbar(
    backgroundColor: Color,
    text: String,
) {
    showSnackSwipe(
        backgroundColor = backgroundColor,
        messageText = {
            Text(
                text = text,
                color = MaterialTheme.LaskColors.warning,
                style = MaterialTheme.LaskTypography.body2,
            )
        },
        icon = {
            Icon(
                imageVector = Icons.Default.Interests,
                contentDescription = null,
                tint = MaterialTheme.LaskColors.warning,
            )
        },
    )
}

fun SnackSwipeController.showErrorSnackbar(
    backgroundColor: Color,
    text: String,
) {
    showSnackSwipe(
        backgroundColor = backgroundColor,
        messageText = {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = text,
                color = MaterialTheme.LaskColors.textPrimary,
                style = MaterialTheme.LaskTypography.body2,
                textAlign = TextAlign.Center
            )
        },
    )
}