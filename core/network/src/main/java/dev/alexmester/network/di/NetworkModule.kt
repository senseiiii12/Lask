package dev.alexmester.network.di

import dev.alexmester.network.BuildConfig
import dev.alexmester.network.plugin.ApiKeyPlugin
import dev.alexmester.network.translate.TranslateApiService
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.ANDROID
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module


val networkModule = module {

    single {

        HttpClient(Android) {

            expectSuccess = true

            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                    coerceInputValues = true
                })
            }

            install(Logging) {
                logger = Logger.ANDROID
                level = LogLevel.HEADERS
            }

            install(HttpTimeout) {
                requestTimeoutMillis = 30_000
                connectTimeoutMillis = 15_000
                socketTimeoutMillis = 30_000
            }

            install(DefaultRequest) {
                url("https://api.worldnewsapi.com/")
                headers.append("Accept", "application/json")
            }

            install(ApiKeyPlugin) {
                this.apiKey = BuildConfig.NEWS_API_KEY
            }

            install(HttpRequestRetry) {
                retryIf { _, response ->
                    response.status.value in 500..599
                }
                exponentialDelay(
                    base = 2.0,
                    maxDelayMs = 10_000,
                )
                maxRetries = 3
            }
        }
    }

    single {
        TranslateApiService(
            client = get(),
            apiKey = BuildConfig.TRANSLATE_PLUS_API_KEY,
        )
    }
}
