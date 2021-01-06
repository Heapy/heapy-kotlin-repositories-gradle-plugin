package io.heapy.gradle.kotlin.repositories

import io.heapy.gradle.kotlin.repositories.KotlinRepositoriesPlugin.Companion.PLUGIN_ID
import org.apache.http.client.methods.HttpHead
import org.apache.http.impl.client.HttpClients
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.extra
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.repositories
import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper
import java.util.Properties
import java.util.concurrent.ConcurrentHashMap

class KotlinRepositoriesPlugin : Plugin<Project> {
    override fun apply(project: Project) = with(project) {
        afterEvaluate {
            val kotlinVersion = kotlinVersionFromClasspath()
            if (kotlinVersion != null) {
                logger.warn("Requested kotlin version [$kotlinVersion]")
                addRepository(kotlinVersion)
            } else {
                logger.error("Kotlin Plugin not found in configuration, plugin will not add any repositories")
            }
        }
    }

    private fun Project.kotlinVersionFromClasspath(): String? {
        val configurationExists = configurations.names.any { it == "implementation" }

        return if (configurationExists) {
            configurations["implementation"].allDependencies.find {
                it.group == "org.jetbrains.kotlin" &&
                        it.name == "kotlin-gradle-plugin"
            }?.version
        } else null
    }

    companion object {
        internal const val PLUGIN_ID = "io.heapy.gradle.kotlin.repositories"
    }
}

private val repositoryMap = ConcurrentHashMap<String, String>()

/**
 * This function can be used in buildSrc plugins
 */
fun Project.kotlinRepository(kotlinVersion: String? = null) {
    val version = kotlinVersion ?: kotlinVersionFromPlugin()

    addRepository(version)
}

private fun kotlinVersionFromPlugin(): String {
    return KotlinPluginWrapper::class.java.classLoader
        .getResource("project.properties")!!
        .openStream().use { propsStream ->
            Properties().let {
                it.load(propsStream)
                it.getProperty("project.version")
            }
        }
}

private const val PROCESSED_PROPERTY = "$PLUGIN_ID.processed"
private fun Project.addRepository(version: String) {
    if (extra.has(PROCESSED_PROPERTY)) return
    extra.set(PROCESSED_PROPERTY, true)

    repositoryMap[version]?.let { repo ->
        logger.warn("Cached: Found kotlin version [$version] in repo [$repo]")
        repositories {
            maven {
                url = uri(repo)
            }
        }
        return
    }

    HttpClients.createMinimal().use { client ->
        val file = "org/jetbrains/kotlin/kotlin-stdlib/${version}/kotlin-stdlib-${version}.jar"

        val repos = listOf(
            "https://jcenter.bintray.com/",
            "https://dl.bintray.com/kotlin/kotlin-eap/",
            "https://dl.bintray.com/kotlin/kotlin-dev/"
        )

        val repo = repos.firstOrNull {
            val get = HttpHead("$it$file")
            val response = client.execute(get)
            response.statusLine.statusCode == 200
        }

        if (repo != null) {
            logger.warn("Found kotlin version [$version] in repo [$repo]")
            repositoryMap[version] = repo
            repositories {
                maven {
                    url = uri(repo)
                }
            }
        } else {
            logger.error("Kotlin version [$version] not found in dev, eap and jcenter repositories")
        }
    }
}
