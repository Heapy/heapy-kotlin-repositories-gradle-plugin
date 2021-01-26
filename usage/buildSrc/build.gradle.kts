plugins {
    `kotlin-dsl`
    id("io.heapy.gradle.kotlin.repositories").version("1.1.0")
}

repositories {
    jcenter()
    gradlePluginPortal()
}

val kotlinVersion = "1.3.70-eap-274"

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
    implementation("io.heapy.gradle.kotlin.repositories:heapy-kotlin-repositories-gradle-plugin:1.1.0")
}
