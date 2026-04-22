import java.util.Properties

plugins {
    id("myapp.android.library")
    id("myapp.ktor")
    id("myapp.koin")
    id("myapp.kotlin.serialization")
}


val localProperties = Properties().apply {
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        load(localPropertiesFile.inputStream())
    }
}

android {
    namespace = "dev.alexmester.network"

    defaultConfig {
        val newsApiKey = localProperties.getProperty("NEWS_API_KEY")
            ?: System.getenv("NEWS_API_KEY")
            ?: ""
        buildConfigField("String", "NEWS_API_KEY", "\"$newsApiKey\"")

        val translateApiKey = localProperties.getProperty("TRANSLATE_PLUS_API_KEY")
            ?: System.getenv("TRANSLATE_PLUS_API_KEY")
            ?: ""
        buildConfigField("String", "TRANSLATE_PLUS_API_KEY", "\"$translateApiKey\"")
    }
}

dependencies {
    implementation(project(":core:models"))

    api(libs.bundles.ktor)
    api(libs.kotlinx.serialization.json)
    api(libs.bundles.koin)
    api(libs.kotlinx.coroutines.core)
}