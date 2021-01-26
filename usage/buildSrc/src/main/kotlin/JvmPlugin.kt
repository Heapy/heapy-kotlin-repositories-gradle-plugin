import io.heapy.gradle.kotlin.repositories.kotlinRepository
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.kotlin
import org.gradle.kotlin.dsl.repositories
import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper

class JvmPlugin : Plugin<Project> {
    override fun apply(project: Project) = with(project) {
        pluginManager.apply(KotlinPluginWrapper::class)

        repositories {
            kotlinRepository()
        }

        dependencies {
            add("implementation", kotlin("stdlib"))
        }
    }
}
