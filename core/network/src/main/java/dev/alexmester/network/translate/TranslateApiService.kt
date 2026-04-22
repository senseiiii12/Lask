package dev.alexmester.network.translate

import dev.alexmester.network.translate.dto.TranslateRequestDto
import dev.alexmester.network.translate.dto.TranslateResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class TranslateApiService(
    private val client: HttpClient,
    private val apiKey: String,
) {
    /**
     * Переводит текст через TranslatePlus v2 API.
     * Документация: https://docs.translateplus.io/reference/v2/translation/translate
     */
    suspend fun translate(
        text: String,
        targetLanguage: String,
        sourceLanguage: String? = null,
    ): TranslateResponseDto = client.post("https://translate.translateplus.io/v2/translate") {
        contentType(ContentType.Application.Json)
        headers {
            append("Authorization", "Bearer $apiKey")
        }
        setBody(
            TranslateRequestDto(
                q = text,
                target = targetLanguage,
                source = sourceLanguage,
            )
        )
    }.body()
}