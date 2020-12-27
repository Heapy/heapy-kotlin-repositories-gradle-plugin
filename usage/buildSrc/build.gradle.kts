plugins {
    `kotlin-dsl`
    id("io.heapy.gradle.kotlin.repositories").version("1.0.0")
}

repositories {
    jcenter()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.70-eap-274")
}
