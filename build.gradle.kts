plugins {
    `kotlin-dsl`
    id("com.gradle.plugin-publish").version("0.12.0")
    id("io.heapy.gradle.properties").version("1.1.2")
    id("java-gradle-plugin")
    `maven-publish`
}

group = "io.heapy.gradle.kotlin.repositories"

repositories {
    jcenter()
}

dependencies {
    compileOnly("org.jetbrains.kotlin:kotlin-gradle-plugin:$embeddedKotlinVersion")
    implementation("org.apache.httpcomponents:httpclient:4.5.13")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.0")
}

// For testing in real project from mavenLocal before publishing
publishing {
    publications {
        create<MavenPublication>("mpl") {
            from(project.components["java"])

            group = "io.heapy.gradle.kotlin.repositories"
            artifactId = "io.heapy.gradle.kotlin.repositories.gradle.plugin"
        }
    }
}

tasks {
    test {
        useJUnitPlatform()
    }
}

gradlePlugin {
    plugins {
        create("propertiesPlugin") {
            id = "io.heapy.gradle.kotlin.repositories"
            implementationClass = "io.heapy.gradle.kotlin.repositories.KotlinRepositoriesPlugin"
            displayName = "Heapy-kotlin-repositories Gradle Plugin"
            description = """
                Plugin that adds necessary kotlin (eap/dev) repositories depends on requested kotlin version.
            """.trimIndent()
        }
    }
}

pluginBundle {
    website = "https://github.com/Heapy/heapy-kotlin-repositories-gradle-plugin"
    vcsUrl = "https://github.com/Heapy/heapy-kotlin-repositories-gradle-plugin"
    tags = listOf("kotlin", "version", "plugin", "eap", "dev")
}
