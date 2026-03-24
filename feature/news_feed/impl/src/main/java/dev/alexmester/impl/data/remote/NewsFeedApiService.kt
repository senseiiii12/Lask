package dev.alexmester.impl.data.remote

import dev.alexmester.impl.data.remote.dto.TopNewsResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class NewsFeedApiService(private val client: HttpClient) {

    /**
     * Топ новостей по стране — используется для главной ленты.
     * [maxNewsPerCluster] — количество статей на одно событие (кластер).
     */
    suspend fun getTopNews(
        sourceCountry: String,
        language: String? = null,
        date: String? = null,
        maxNewsPerCluster: Int = 5,
    ): TopNewsResponseDto = client.get("top-news") {
        parameter("source-country", sourceCountry)
        parameter("language", language)
        parameter("date", date)
        parameter("headlines-only", false)
        parameter("max-news-per-cluster", maxNewsPerCluster)
    }.body()

}