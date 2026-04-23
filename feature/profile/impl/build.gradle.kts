plugins {
    id("myapp.android.library")
    id("myapp.android.compose")
    id("myapp.koin")
    id("myapp.kotlin.serialization")
    id("myapp.room")
    id("myapp.coil")
}

android {
    namespace = "dev.alexmester.profile.impl"
}

dependencies {
    api(project(":feature:profile:api"))
    implementation(project(":feature:article_detail:api"))

    implementation(project(":core:database"))
    implementation(project(":core:models"))
    implementation(project(":core:ui"))
    implementation(project(":core:datastore"))
    implementation(project(":core:utils"))
}