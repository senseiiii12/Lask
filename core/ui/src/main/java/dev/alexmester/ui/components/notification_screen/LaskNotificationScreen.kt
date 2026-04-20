package dev.alexmester.ui.components.notification_screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.alexmester.error.NetworkErrorUiMapper
import dev.alexmester.error.NotificationUi
import dev.alexmester.models.error.NetworkError
import dev.alexmester.ui.R
import dev.alexmester.ui.desing_system.LaskColors
import dev.alexmester.ui.desing_system.LaskTheme
import dev.alexmester.ui.desing_system.LaskTypography

sealed interface NotificationType {
    data class Error(val error: NetworkError) : NotificationType
    data class Warning(val text: String, val image: ImageVector) : NotificationType
}

@Composable
fun LaskNotificationScreen(
    modifier: Modifier = Modifier,
    type: NotificationType,
    showRetry: Boolean = false,
    isRetrying: Boolean = false,
    onRetry: () -> Unit = {}
) {
    val ui = when (type) {
        is NotificationType.Error -> NetworkErrorUiMapper.mapToNotificationUi(type.error)
        is NotificationType.Warning -> NotificationUi(
            image = type.image,
            text = type.text,
            tint = MaterialTheme.LaskColors.warning
        )
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        AnimatedContent(
            targetState = isRetrying,
            contentAlignment = Alignment.Center,
            transitionSpec = {
                fadeIn(tween(150)) + scaleIn(tween(150), 0.8f) togetherWith
                fadeOut(tween(150)) + scaleOut(tween(150), 0.8f)
            },
            label = "errorContent",
        ) { isRefreshing ->
            if (isRefreshing) {
                CircularProgressIndicator(
                    color = MaterialTheme.LaskColors.brand_blue,
                    trackColor = MaterialTheme.LaskColors.brand_blue10,
                )
            } else {
                NotificationLayout(
                    image = ui.image,
                    text = ui.text,
                    tint = ui.tint,
                    showRetry = showRetry,
                    onRetry = onRetry
                )
            }
        }
    }
}

@Composable
fun NotificationLayout(
    image: ImageVector,
    text: String,
    tint: Color,
    showRetry: Boolean,
    onRetry: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Icon(
            modifier = Modifier.size(80.dp),
            imageVector = image,
            contentDescription = null,
            tint = tint
        )
        Text(
            text = text,
            style = MaterialTheme.LaskTypography.footnoteSemiBold,
            color = tint,
            textAlign = TextAlign.Center
        )

        if (showRetry) {
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.LaskColors.brand_blue10,
                ),
                contentPadding = PaddingValues(vertical = 12.dp, horizontal = 32.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = stringResource(R.string.error_retry),
                    style = MaterialTheme.LaskTypography.button2,
                    color = MaterialTheme.LaskColors.textPrimary,
                )
            }
        }
    }
}


@Preview
@Composable
private fun ErrorDark() {
    LaskTheme(darkTheme = true) {
        LaskNotificationScreen(
            type = NotificationType.Error(NetworkError.NoInternet()),
            showRetry = true,
            isRetrying = false,
            onRetry = {}
        )
    }
}
@Preview
@Composable
private fun WarningDark() {
    LaskTheme(darkTheme = true) {
        LaskNotificationScreen(
            type = NotificationType.Warning(
                text = "Empty bookmark",
                image = Icons.Default.Bookmarks
            ),
            showRetry = true,
            isRetrying = false,
            onRetry = {}
        )
    }
}