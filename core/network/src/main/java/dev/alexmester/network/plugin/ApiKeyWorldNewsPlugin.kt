package dev.alexmester.network.plugin

import io.ktor.client.plugins.api.ClientPlugin
import io.ktor.client.plugins.api.createClientPlugin

val ApiKeyWorldNewsPlugin: ClientPlugin<ApiKeyWorldNewsConfig> = createClientPlugin(
    name = "ApiKeyWorldNewsPlugin",
    createConfiguration = ::ApiKeyWorldNewsConfig,
) {
    val apiKey = pluginConfig.apiKey

    onRequest { request, _ ->
        request.url.parameters.append("api-key", apiKey)
    }
}

class ApiKeyWorldNewsConfig {
    var apiKey: String = ""
}
