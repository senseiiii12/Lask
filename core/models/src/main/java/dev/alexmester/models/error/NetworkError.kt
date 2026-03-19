package dev.alexmester.models.error


sealed class NetworkError(
    message: String? = null,
    cause: Throwable? = null,
) : Exception(message, cause) {

    class NoInternet : NetworkError()

    class Unauthorized : NetworkError()

    class Timeout : NetworkError()

    data class HttpError(
        val code: Int,
        override val message: String? = null,
    ) : NetworkError(message)

    data class RateLimit(
        val retryAfterSeconds: Long? = null,
    ) : NetworkError()

    data class ParseError(
        override val cause: Throwable? = null,
    ) : NetworkError(cause = cause)

    data class Unknown(
        override val message: String? = null,
        override val cause: Throwable? = null,
    ) : NetworkError(message, cause)
}