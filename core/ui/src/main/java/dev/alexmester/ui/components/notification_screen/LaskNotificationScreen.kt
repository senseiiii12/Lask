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
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.alexmester.error.NetworkErrorUiMapper
import dev.alexmester.models.error.NetworkError
import dev.alexmester.ui.R
import dev.alexmester.ui.desing_system.LaskColors
import dev.alexmester.ui.desing_system.LaskTheme
import dev.alexmester.ui.desing_system.LaskTypography
import dev.alexmester.ui.uitext.UiText

enum class LayoutVariants {
    ERROR, WARNING
}

@Composable
fun LaskNotificationScreen(
    modifier: Modifier = Modifier,
    imageWarning: ImageVector = Icons.Default.Image,
    textWarning: String = "",
    errorType: NetworkError = NetworkError.Unknown(),
    layoutVariants: LayoutVariants = LayoutVariants.ERROR,
    showRetry: Boolean = true,
    isRetrying: Boolean = false,
    onRetry: () -> Unit = {}
) {

    val imageError: ImageVector = when (errorType) {
        is NetworkError.NoInternet -> ImageVector.vectorResource(R.drawable.ic_no_internet_error)
        is NetworkError.PaymentRequired -> ImageVector.vectorResource(R.drawable.ic_payment_required)
        is NetworkError.RateLimit -> ImageVector.vectorResource(R.drawable.ic_rate_limit_error)
        is NetworkError.Unknown -> ImageVector.vectorResource(R.drawable.ic_unknow_error)
        else -> {}
    } as ImageVector

    val textError: UiText = NetworkErrorUiMapper.toUiText(errorType)

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        AnimatedContent(
            targetState = isRetrying,
            contentAlignment = Alignment.Center,
            transitionSpec = {
                fadeIn(tween(150)) + scaleIn(
                    tween(150),
                    initialScale = 0.8f,
                ) togetherWith fadeOut(tween(150)) + scaleOut(
                    tween(150),
                    targetScale = 0.8f,
                )
            },
            label = "errorContent",
        ) { isRefreshing ->
            if (isRefreshing) {
                CircularProgressIndicator(
                    color = MaterialTheme.LaskColors.brand_blue,
                    trackColor = MaterialTheme.LaskColors.brand_blue10,
                )
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(32.dp),
                ) {
                    when(layoutVariants){
                        LayoutVariants.ERROR ->{
                            ErrorLayout(
                                modifier = Modifier,
                                imageError = imageError,
                                textError = textError.asString(),
                                showRetry = showRetry,
                                onRetry = onRetry
                            )
                        }

                        LayoutVariants.WARNING -> {
                            WarningLayout(
                                modifier = Modifier,
                                textWarning = textWarning,
                                imageWarning = imageWarning,
                                showRetry = showRetry,
                                onRetry = onRetry
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ErrorLayout(
    modifier: Modifier = Modifier,
    imageError: ImageVector,
    textError: String,
    showRetry: Boolean,
    onRetry: () -> Unit,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Icon(
            modifier = Modifier.size(80.dp),
            imageVector = imageError,
            contentDescription = null,
            tint = MaterialTheme.LaskColors.error
        )
        Text(
            text = textError,
            style = MaterialTheme.LaskTypography.footnoteSemiBold,
            color = MaterialTheme.LaskColors.error,
            textAlign = TextAlign.Center
        )
    }
    if (showRetry){
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
@Composable
fun WarningLayout(
    modifier: Modifier = Modifier,
    textWarning: String,
    imageWarning: ImageVector,
    showRetry: Boolean,
    onRetry: () -> Unit,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Icon(
            modifier = Modifier.size(80.dp),
            imageVector = imageWarning,
            contentDescription = null,
            tint = MaterialTheme.LaskColors.warning
        )
        Text(
            text = textWarning,
            style = MaterialTheme.LaskTypography.footnoteSemiBold,
            color = MaterialTheme.LaskColors.warning,
            textAlign = TextAlign.Center
        )
    }
    if (showRetry){
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


@Preview
@Composable
private fun ErrorDark() {
    LaskTheme(darkTheme = true) {
        LaskNotificationScreen(
            imageWarning = Icons.Default.Bookmarks,
            textWarning = "Bookmarks is empty",
            errorType = NetworkError.NoInternet(),
            layoutVariants = LayoutVariants.ERROR,
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
            imageWarning = Icons.Default.Bookmarks,
            textWarning = "Bookmarks is empty",
            errorType = NetworkError.NoInternet(),
            layoutVariants = LayoutVariants.WARNING,
            showRetry = true,
            isRetrying = false,
            onRetry = {}
        )
    }
}