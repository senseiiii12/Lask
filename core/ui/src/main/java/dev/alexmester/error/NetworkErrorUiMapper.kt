package dev.alexmester.error

import dev.alexmester.models.error.NetworkError
import dev.alexmester.ui.R
import dev.alexmester.ui.uitext.UiText

object NetworkErrorUiMapper {
    fun toUiText(error: NetworkError): UiText = when (error) {
        is NetworkError.NoInternet -> UiText.StringResource(R.string.error_no_internet)
        is NetworkError.PaymentRequired -> UiText.StringResource(R.string.error_payment_required)
        is NetworkError.RateLimit -> UiText.StringResource(R.string.error_rate_limit)
        else -> UiText.StringResource(R.string.error_unknown)
    }
}