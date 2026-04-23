plugins {
    id("myapp.android.library")
    id("myapp.android.compose")
    id("myapp.koin")
    id("myapp.kotlin.serialization")
    id("myapp.room")
    id("myapp.ktor")
    id("myapp.coil")
}

android {
    namespace = "dev.alexmester.news_feed.impl"
}

dependencies {
    api(project(":feature:news_feed:api"))
    implementation(project(":feature:article_detail:api"))

    implementation(project(":core:network"))
    implementation(project(":core:database"))
    implementation(project(":core:models"))
    implementation(project(":core:ui"))
    implementation(project(":core:datastore"))
    implementation(project(":core:utils"))

    implementation("com.github.AlexMester25:SwipeSnack:0.3.3")
}