plugins {
    id("myapp.android.library")
    id("myapp.android.compose")
    id("myapp.koin")
    id("myapp.kotlin.serialization")
    id("myapp.room")
    id("myapp.coil")
    id("myapp.media3")
}

android {
    namespace = "dev.alexmester.article_detail.impl"
}

dependencies {
    api(project(":feature:article_detail:api"))
    implementation(project(":core:network"))
    implementation(project(":core:database"))
    implementation(project(":core:datastore"))
    implementation(project(":core:models"))
    implementation(project(":core:ui"))
    implementation(project(":core:utils"))

    implementation("com.github.AlexMester25:SwipeSnack:0.3.3")
}