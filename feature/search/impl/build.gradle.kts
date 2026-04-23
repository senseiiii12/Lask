plugins {
    id("myapp.android.library")
    id("myapp.android.compose")
    id("myapp.koin")
    id("myapp.kotlin.serialization")
    id("myapp.ktor")
    id("myapp.coil")
}

android {
    namespace = "dev.alexmester.search.impl"
}

dependencies {
    api(project(":feature:search:api"))
    implementation(project(":feature:article_detail:api"))

    implementation(project(":core:network"))
    implementation(project(":core:models"))
    implementation(project(":core:database"))
    implementation(project(":core:ui"))
    implementation(project(":core:datastore"))
    implementation(project(":core:utils"))
}