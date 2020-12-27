package io.heapy.gradle.kotlin.repositories

import org.apache.http.client.methods.HttpHead
import org.apache.http.impl.client.HttpClients
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.repositories

class KotlinRepositoriesPlugin : Plugin<Project> {
    override fun apply(project: Project) = with(project) {
        afterEvaluate {
            val kotlinVersion = configurations["implementation"].allDependencies.find {
                it.group == "org.jetbrains.kotlin" &&
                        it.name == "kotlin-gradle-plugin"
            }?.version

            if (kotlinVersion != null) {
                logger.warn("Requested kotlin version [$kotlinVersion]")
                applyRepository(kotlinVersion)
            } else {
                logger.error("Kotlin Plugin not found in configuration, plugin will not add any repositories")
            }
        }
    }

    companion object {
        internal const val PLUGIN_ID = "io.heapy.gradle.kotlin.repositories"
    }
}

/**
 * This function can be used in buildSrc plugins
 */
fun Project.applyRepository(version: String) {
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
