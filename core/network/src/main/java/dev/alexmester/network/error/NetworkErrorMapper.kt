package dev.alexmester.network.error

import dev.alexmester.models.error.NetworkError
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.ResponseException
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.SerializationException
import java.net.ConnectException
import java.net.SocketException
import java.net.UnknownHostException
import java.nio.channels.UnresolvedAddressException

/**
 * Централизованный маппинг Ktor/Java исключений в типизированный NetworkError.
 *
 * Вызывается в runAppResult { } блоках в data-слоях.
 * Все feature-модули работают только с NetworkError, никакого Ktor в :impl слоях.
 */
internal object NetworkErrorMapper {

    fun map(throwable: Throwable): NetworkError = when (throwable) {
        is NetworkError -> throwable

        is ResponseException -> when (throwable.response.status.value) {
            401 -> NetworkError.Unauthorized()
            402 -> NetworkError.PaymentRequired()
            422 -> NetworkError.HttpError(
                code = 422,
                message = "Text is too long for translation"
            )
            429 -> {
                val retryAfter = throwable.response.headers["Retry-After"]?.toLongOrNull()
                NetworkError.RateLimit(retryAfterSeconds = retryAfter)
            }
            in 400..499 -> NetworkError.HttpError(
                code = throwable.response.status.value,
                message = throwable.response.status.description,
            )
            in 500..599 -> NetworkError.HttpError(
                code = throwable.response.status.value,
                message = throwable.response.status.description,
            )
            else -> NetworkError.HttpError(code = throwable.response.status.value)
        }

        is HttpRequestTimeoutException -> NetworkError.Timeout()
        is UnknownHostException -> NetworkError.NoInternet()
        is UnresolvedAddressException -> NetworkError.NoInternet()
        is ConnectException -> NetworkError.NoInternet()
        is SocketException -> NetworkError.NoInternet()
        is SerializationException -> NetworkError.ParseError(cause = throwable)
        else -> NetworkError.Unknown(cause = throwable, message = throwable.message)
    }
}