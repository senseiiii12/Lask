plugins {
    id("myapp.android.library")
}

android {
    namespace = "dev.alexmester.platform"
}

dependencies{
    implementation(project(":core:models"))
    implementation(project(":core:utils"))
    implementation(project(":core:datastore"))
}

