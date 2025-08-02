import dev.slne.surf.surfapi.gradle.util.slneReleases

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
}

kotlin {
    jvmToolchain(24)
}

publishing {
    repositories {
        slneReleases()
    }
}