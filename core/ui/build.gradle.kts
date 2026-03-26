plugins {
    id("myapp.android.library")
    id("myapp.android.compose")
}

android {
    namespace = "dev.alexmester.ui"
}

dependencies{
    api("com.google.accompanist:accompanist-systemuicontroller:0.34.0")
    api("dev.chrisbanes.haze:haze:1.7.2")
    api("dev.chrisbanes.haze:haze-materials:1.7.2")
}

