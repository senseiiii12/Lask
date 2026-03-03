plugins {
    id("myapp.android.library")
    id("myapp.android.compose")
    id("myapp.koin")
    id("myapp.kotlin.serialization")
}

android {
    namespace = "com.koin.posts"
}





dependencies {
    implementation(project(":core:network"))
    implementation(project(":core:database"))
}