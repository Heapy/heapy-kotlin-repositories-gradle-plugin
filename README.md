# Heapy-kotlin-repositories Gradle Plugin [![Build Status](https://travis-ci.com/Heapy/heapy-kotlin-repositories-gradle-plugin.svg?branch=main)](https://travis-ci.com/Heapy/heapy-kotlin-repositories-gradle-plugin) [![Gradle Plugin Portal](https://img.shields.io/maven-metadata/v/https/plugins.gradle.org/m2/io/heapy/gradle/kotlin/repositories/io.heapy.gradle.kotlin.repositories.gradle.plugin/maven-metadata.xml.svg?colorB=007ec6&label=gradle%20plugin)](https://plugins.gradle.org/plugin/io.heapy.gradle.kotlin.repositories)

## Deprecated

Eap and Dev repositories gone, EAP releases published on Maven Central

## For buildSrc usage ‼️

Why buildSrc? Reason is simple: in gradle plugin it's not possible to work with [`pluginManagement`](https://docs.gradle.org/current/userguide/plugins.html#sec:plugin_version_management).

Roughly gradle works this way:

1. Execute [`init.gradle.kts`](https://docs.gradle.org/current/userguide/init_scripts.html#init_scripts)
1. Execute `settings.gradle.kts`
1. Parse `plugins` section of `build.gradle.kts`
1. Execute `build.gradle.kts`

So plugin executes after `plugins` resolved, so plugin can't add Kotlin repository to plugin repositories.

## Usage

```kotlin
// buildSrc/build.gradle.kts
plugins {
    `kotlin-dsl`
    // Plugin will add repository to current build to download kotlin-gradle-plugin
    id("io.heapy.gradle.kotlin.repositories").version("1.1.0")
}

repositories {
    jcenter()
    gradlePluginPortal()
}

// This is single place where version should be updated
val kotlinVersion = "1.4.20-dev-3947"

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
    // Add plugin to classpath to use functions in `JvmPlugin`
    implementation("io.heapy.gradle.kotlin.repositories:heapy-kotlin-repositories-gradle-plugin:1.1.0")
}
```

```kotlin
// build.gradle.kts
plugins {
    id("jvm-plugin")
}
```

```kotlin
// buildSrc/src/main/kotlin/JvmPlugin.kt
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
```

## Configuration

Kotlin version will be automatically pickup from `kotlin-gradle-plugin` version in classpath.
No additional configuration required.

## Examples

Checkout [usage](./usage) for example usage.
