import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.dependencies

class CoilConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            val libs = extensions.getByType(VersionCatalogsExtension::class.java).named("libs")
            dependencies {
                add("implementation", libs.findLibrary("coil.compose").get())
                add("implementation", libs.findLibrary("coil.core").get())
                add("implementation", libs.findLibrary("coil.network.okhttp").get())
            }
        }
    }
}
 