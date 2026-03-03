import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.dependencies

class KtorConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            val libs = extensions.getByType(VersionCatalogsExtension::class.java).named("libs")
            dependencies {
                add("implementation", libs.findLibrary("ktor.client.core").get())
                add("implementation", libs.findLibrary("ktor.client.android").get())
                add("implementation", libs.findLibrary("ktor.client.content.negotiation").get())
                add("implementation", libs.findLibrary("ktor.client.logging").get())
                add("implementation", libs.findLibrary("ktor.serialization.kotlinx.json").get())
            }
        }
    }
}