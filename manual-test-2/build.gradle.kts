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
