package dev.alexmester.network.plugin

import io.ktor.client.plugins.api.ClientPlugin
import io.ktor.client.plugins.api.createClientPlugin

val ApiKeyTranslatePlugin: ClientPlugin<ApiKeyTranslateConfig> = createClientPlugin(
    name = "ApiKeyTranslatePlugin",
    createConfiguration = ::ApiKeyTranslateConfig,
) {
    val apiKey = pluginConfig.apiKey

    onRequest { request, _ ->
        request.headers.append("X-API-KEY", apiKey)
    }
}

class ApiKeyTranslateConfig {
    var apiKey: String = ""
}
