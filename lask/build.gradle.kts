plugins {
    id("myapp.android.application")
    id("myapp.android.compose")
    id("myapp.koin")
    id("myapp.ktor")
    id("myapp.kotlin.serialization")
    id("myapp.coil")
//    alias(libs.plugins.baselineprofile)
}

android {
    namespace = "dev.alexmester.lask"

    defaultConfig {
        applicationId = "dev.alexmester.lask"
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildFeatures {
        buildConfig = true
    }
    
    buildTypes {
        release {
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }
        create("benchmark") {
            initWith(buildTypes.getByName("release"))
            signingConfig = signingConfigs.getByName("debug")
            isDebuggable = false
            matchingFallbacks += listOf("release")
        }
        debug {
            isMinifyEnabled = false
        }
    }
}

dependencies {
    implementation(project(":core:network"))
    implementation(project(":core:database"))
    implementation(project(":core:ui"))
    implementation(project(":core:datastore"))
    implementation(project(":core:models"))
    implementation(project(":core:utils"))
    implementation(project(":core:platform"))

    implementation(project(":feature:news_feed:impl"))
    implementation(project(":feature:explore:impl"))
    implementation(project(":feature:article_detail:impl"))
    implementation(project(":feature:bookmarks:impl"))
    implementation(project(":feature:profile:impl"))
    implementation(project(":feature:search:impl"))


    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation("androidx.profileinstaller:profileinstaller:1.4.1")

//    "baselineProfile"(project(":macrobenchmark"))
}
// Настройки генерации профиля
//baselineProfile {
//    baselineProfileOutputDir = "src/main"
//    automaticGenerationDuringBuild = false
//    saveInSrc = true
//    mergeIntoMain = true
//}