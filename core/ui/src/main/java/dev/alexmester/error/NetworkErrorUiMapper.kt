package dev.alexmester.error

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import dev.alexmester.models.error.NetworkError
import dev.alexmester.ui.R
import dev.alexmester.ui.desing_system.LaskColors
import dev.alexmester.ui.uitext.UiText

data class NotificationUi(
    val image: ImageVector,
    val text: String,
    val tint: Color
)

object NetworkErrorUiMapper {

    fun toUiText(error: NetworkError): UiText = when (error) {
        is NetworkError.NoInternet -> UiText.StringResource(R.string.error_no_internet)
        is NetworkError.PaymentRequired -> UiText.StringResource(R.string.error_payment_required)
        is NetworkError.RateLimit -> UiText.StringResource(R.string.error_rate_limit)
        is NetworkError.HttpError -> when (error.code) {
            422 -> UiText.StringResource(R.string.error_translation_text_too_long)
            else -> UiText.StringResource(R.string.error_translation_failed)
        }
        else -> UiText.StringResource(R.string.error_unknown)
    }

    @Composable
    fun mapToNotificationUi(error: NetworkError): NotificationUi {
        return when (error) {
            is NetworkError.NoInternet -> NotificationUi(
                image = ImageVector.vectorResource(R.drawable.ic_no_internet_error),
                text = toUiText(error).asString(),
                tint = MaterialTheme.LaskColors.error
            )

            is NetworkError.PaymentRequired -> NotificationUi(
                image = ImageVector.vectorResource(R.drawable.ic_payment_required),
                text = toUiText(error).asString(),
                tint = MaterialTheme.LaskColors.error
            )

            is NetworkError.RateLimit -> NotificationUi(
                image = ImageVector.vectorResource(R.drawable.ic_rate_limit_error),
                text = toUiText(error).asString(),
                tint = MaterialTheme.LaskColors.error
            )

            is NetworkError.Unknown -> NotificationUi(
                image = ImageVector.vectorResource(R.drawable.ic_unknow_error),
                text = toUiText(error).asString(),
                tint = MaterialTheme.LaskColors.error
            )
            else -> {}
        } as NotificationUi
    }
}
