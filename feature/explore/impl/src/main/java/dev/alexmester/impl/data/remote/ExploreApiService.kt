package dev.alexmester.impl.data.remote

import dev.alexmester.impl.data.remote.dto.SearchNewsResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class ExploreApiService(
    private val client: HttpClient,
) {

    suspend fun searchNews(
        text: String,
        language: String,
        offset: Int,
        number: Int,
    ): SearchNewsResponseDto =
        client.get("search-news") {
            parameter("text", text)
            parameter("text-match-indexes", "title")
            parameter("language", language)
            parameter("offset", offset)
            parameter("number", number)
        }.body()
}