package io.heapy.gradle.kotlin.repositories

import org.gradle.testfixtures.ProjectBuilder
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path

class KotlinRepositoriesPluginTest {
    @Test
    fun `test apply plugin`() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply(KotlinRepositoriesPlugin.PLUGIN_ID)

        assertNotNull(project.plugins.getPlugin(KotlinRepositoriesPlugin::class.java))
    }

    @Test
    fun `simple project build with dev version`() {
        val projectDir = Files.createTempDirectory("")
        projectDir.addFile(file = "build.gradle.kts") {
            """
                plugins {
                    `kotlin-dsl`
                    id("io.heapy.gradle.kotlin.repositories")
                }
                
                repositories {
                    jcenter()
                }
                
                dependencies {
                    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.20-dev-3947")
                }
            """.trimIndent()
        }

        val result = GradleRunner.create()
            .withProjectDir(projectDir.toFile())
            .withPluginClasspath()
            .withArguments("build")
            .build()

        assertEquals(TaskOutcome.SUCCESS, result.task(":build")?.outcome)
        assertTrue(result.output.contains("Requested kotlin version [1.4.20-dev-3947]"))
        assertTrue(result.output.contains("Found kotlin version [1.4.20-dev-3947] in repo [https://dl.bintray.com/kotlin/kotlin-dev/]"))
    }

    @Test
    fun `simple project build with eap version`() {
        val projectDir = Files.createTempDirectory("")
        projectDir.addFile(file = "build.gradle.kts") {
            """
                plugins {
                    `kotlin-dsl`
                    id("io.heapy.gradle.kotlin.repositories")
                }
                
                repositories {
                    jcenter()
                }
                
                dependencies {
                    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.70-eap-274")
                }
            """.trimIndent()
        }

        val result = GradleRunner.create()
            .withProjectDir(projectDir.toFile())
            .withPluginClasspath()
            .withArguments("build")
            .build()

        assertEquals(TaskOutcome.SUCCESS, result.task(":build")?.outcome)
        val s1 = "Requested kotlin version [1.3.70-eap-274]"
        assertTrue(result.output.contains(s1)) {
            "Output doesn't contains [$s1]. Output: ${result.output}"
        }
        val s2 = "Found kotlin version [1.3.70-eap-274] in repo [https://dl.bintray.com/kotlin/kotlin-eap/]"
        assertTrue(result.output.contains(s2)) {
            "Output doesn't contains [$s2]. Output: ${result.output}"
        }
    }

    @Test
    fun `simple project build unknown version`() {
        val projectDir = Files.createTempDirectory("")
        projectDir.addFile(file = "build.gradle.kts") {
            """
                plugins {
                    `kotlin-dsl`
                    id("io.heapy.gradle.kotlin.repositories")
                }
                
                repositories {
                    jcenter()
                }
                
                dependencies {
                    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:unknown")
                }
            """.trimIndent()
        }

        val result = GradleRunner.create()
            .withProjectDir(projectDir.toFile())
            .withPluginClasspath()
            .withArguments("build")
            .buildAndFail()

        val s1 = "Requested kotlin version [unknown]"
        assertTrue(result.output.contains(s1)) {
            "Output doesn't contains [$s1]. Output: ${result.output}"
        }
        val s2 = "Kotlin version [unknown] not found in dev, eap or jcenter repositories"
        assertTrue(result.output.contains(s2)) {
            "Output doesn't contains [$s2]. Output: ${result.output}"
        }
    }

    @Test
    fun `simple project build with kotlin version`() {
        val projectDir = Files.createTempDirectory("")
        Files.createDirectories(projectDir.resolve("buildSrc"))
        projectDir.addFile("buildSrc", file = "build.gradle.kts") {
            """
                plugins {
                    `kotlin-dsl`
                    id("io.heapy.gradle.kotlin.repositories").version("1.1.0")
                }

                repositories {
                    jcenter()
                    gradlePluginPortal()
                }

                dependencies {
                    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.70-eap-274")
                    implementation("io.heapy.gradle.kotlin.repositories:heapy-kotlin-repositories-gradle-plugin:1.1.0")
                }
            """.trimIndent()
        }
        projectDir.addFile(file = "build.gradle.kts") {
            """
                import io.heapy.gradle.kotlin.repositories.kotlinRepository
                import org.gradle.api.internal.artifacts.repositories.DefaultMavenArtifactRepository

                repositories {
                    kotlinRepository()
                    jcenter()
                }

                tasks.register("validate") {
                    val eapRepo = repositories.map { it as DefaultMavenArtifactRepository }
                        .firstOrNull { it.url == uri("https://dl.bintray.com/kotlin/kotlin-eap/") }

                    if (eapRepo != null) println("Found eap")
                }
            """.trimIndent()
        }

        val result = GradleRunner.create()
            .withProjectDir(projectDir.toFile())
            .withPluginClasspath()
            .withArguments("validate")
            .build()

        assertTrue(result.output.contains("Found eap"))
    }
}

internal fun Path.addFile(vararg folders: String, file: String, body: () -> String) {
    val folder = folders.fold(this) { acc, path ->
        acc.resolve(path)
    }
    Files.createDirectories(folder)
    val newFile = folder.resolve(file).toFile()
    newFile.writeText(body())
}
