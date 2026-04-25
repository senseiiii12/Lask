plugins {
    id("myapp.android.library")
}

android {
    namespace = "dev.alexmester.utils"
}

dependencies{
    implementation(project(":core:models"))
}