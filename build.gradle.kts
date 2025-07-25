import dev.slne.surf.surfapi.gradle.util.slneReleases
import org.gradle.api.tasks.testing.logging.TestExceptionFormat

plugins {
    id("dev.slne.surf.surfapi.gradle.core")
}

group = "dev.slne.surf"
version = findProperty("version") as String

dependencies {
    api(libs.bundles.exposed) {
        exclude("org.jetbrains.kotlin", "kotlin-stdlib")
        exclude("org.jetbrains.kotlin", "kotlin-reflect")
        exclude("org.jetbrains.kotlinx", "kotlinx-coroutines-core")
        exclude("org.slf4j", "slf4j-api")
    }
    api(libs.hikari)
    api(libs.lettuce)

    runtimeOnly(libs.sqlite)
    runtimeOnly(libs.mariadb)

    testImplementation(platform("org.junit:junit-bom:5.13.4"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation(kotlin("test"))
    testImplementation(kotlin("serialization"))
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactive:1.10.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.8.22")
    testImplementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.22")
    testImplementation("dev.slne.surf:surf-api-standalone:1.21.7+")
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
        showStandardStreams = true
        exceptionFormat = TestExceptionFormat.FULL
    }
}

kotlin {
    jvmToolchain(24)
}

publishing {
    repositories {
        slneReleases()
    }
}