package dev.alexmester.network.di

import dev.alexmester.network.BuildConfig
import dev.alexmester.network.plugin.ApiKeyWorldNewsPlugin
import dev.alexmester.network.plugin.ApiKeyTranslatePlugin
import dev.alexmester.network.translate.TranslateApiService
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.ANDROID
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.qualifier.named
import org.koin.dsl.module

fun createBaseClient() = HttpClient(Android) {
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

enum class Clients{
    WORLD_NEWS,
    TRANSLATE,
}

val networkModule = module {

    single(named(Clients.WORLD_NEWS)) {
        createBaseClient().config {

            install(DefaultRequest) {
                url("https://api.worldnewsapi.com/")
                headers.append("Accept", "application/json")
            }

            install(ApiKeyWorldNewsPlugin) {
                apiKey = BuildConfig.NEWS_API_KEY
            }
        }
    }

    single(named(Clients.TRANSLATE)) {
        createBaseClient().config {

            install(DefaultRequest) {
                url("https://api.translateplus.io/")
                headers.append("Accept", "application/json")
            }

            install(ApiKeyTranslatePlugin) {
                apiKey = BuildConfig.TRANSLATE_PLUS_API_KEY
            }
        }
    }

    single {
        TranslateApiService(
            client = get(named(Clients.TRANSLATE)),
        )
    }
}
