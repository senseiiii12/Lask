plugins {
    id("myapp.android.library")
    id("myapp.android.compose")
}

android {
    namespace = "dev.alexmester.utils"
}

dependencies{
    implementation(project(":core:models"))
    implementation(project(":core:datastore"))
}