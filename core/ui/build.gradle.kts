plugins {
    id("myapp.android.library")
    id("myapp.android.compose")
    id("myapp.coil")
}

android {
    namespace = "dev.alexmester.ui"
}

dependencies{
    implementation(project(":core:models"))
    implementation(project(":core:utils"))

    api("com.google.accompanist:accompanist-systemuicontroller:0.34.0")
    api("dev.chrisbanes.haze:haze:1.7.2")
    api("dev.chrisbanes.haze:haze-materials:1.7.2")
    api("com.github.AlexMester25:SwipeSnack:0.3.3")
}

