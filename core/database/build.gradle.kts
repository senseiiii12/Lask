plugins {
    id("myapp.android.library")  // заменяет android.library + общие зависимости
    id("myapp.room")             // заменяет ksp + room зависимости
    id("myapp.koin")             // заменяет koin зависимости
}

android {
    namespace = "com.koin.database"
}

dependencies {
    api(libs.bundles.room)
    api(libs.bundles.koin)
    api(libs.kotlinx.coroutines.core)
}