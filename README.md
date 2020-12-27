# Heapy-kotlin-repositories Gradle Plugin [![Build Status](https://travis-ci.com/Heapy/heapy-kotlin-repositories-gradle-plugin.svg?branch=main)](https://travis-ci.com/Heapy/heapy-kotlin-repositories-gradle-plugin) [![Gradle Plugin Portal](https://img.shields.io/maven-metadata/v/https/plugins.gradle.org/m2/io/heapy/gradle/kotlin/repositories/io.heapy.gradle.kotlin.repositories/maven-metadata.xml.svg?colorB=007ec6&label=gradle%20plugin)](https://plugins.gradle.org/plugin/io.heapy.gradle.kotlin.repositories)

## For buildSrc usage ‼️

Why buildSrc? Reason is simple: in gradle plugin it's not possible to work with [`pluginManagement`](https://docs.gradle.org/current/userguide/plugins.html#sec:plugin_version_management).

Roughly gradle works this way:

1. Execute [`init.gradle.kts`](https://docs.gradle.org/current/userguide/init_scripts.html#init_scripts)
1. Execute `settings.gradle.kts`
1. Parse `plugins` section of `build.gradle.kts`
1. Execute `build.gradle.kts`

So plugin executes after `plugins` resolved, so plugin can't add Kotlin repository to plugin repositories.

## Install

```kotlin
// buildSrc/build.gradle.kts
plugins {
    id("io.heapy.gradle.kotlin.repositories").version("1.0.0")
}

val kotlinVersion = "1.4.20-dev-3947"

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
}
```

## Configuration

Kotlin version will be automatically pickup from `kotlin-gradle-plugin` version in classpath.
No additional configuration required.

## Examples

Checkout [usage](./usage) for example usage.
