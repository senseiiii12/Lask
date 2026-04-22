package dev.alexmester.network.translate.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TranslateRequestDto(
    val q: String,
    val target: String,
    val source: String? = null,
)

@Serializable
data class TranslateResponseDto(
    val data: TranslateDataDto,
)

@Serializable
data class TranslateDataDto(
    val translations: List<TranslationDto>,
)

@Serializable
data class TranslationDto(
    @SerialName("translatedText") val translatedText: String,
    @SerialName("detectedSourceLanguage") val detectedSourceLanguage: String? = null,
)