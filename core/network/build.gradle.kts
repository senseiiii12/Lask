plugins {
    id("myapp.android.library")
    id("myapp.ktor")
    id("myapp.koin")
    id("myapp.kotlin.serialization")
}

android {
    namespace = "com.koin.network"
}

dependencies {
    api(libs.bundles.ktor)
    api(libs.kotlinx.serialization.json)
    api(libs.bundles.koin)
    api(libs.kotlinx.coroutines.core)
}