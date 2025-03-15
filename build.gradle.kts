import dev.slne.surf.surfapi.gradle.util.slneSnapshots

plugins {
    id("dev.slne.surf.surfapi.gradle.core") version "1.21.4-1.0.121"
}

group = "dev.slne.surf"
version = findProperty("version") as String

tasks.shadowJar {
    archiveFileName.set("surf-database.jar")
}

dependencies {
    api(libs.bundles.exposed) {
        exclude("org.jetbrains.kotlin", "kotlin-stdlib")
        exclude("org.jetbrains.kotlin", "kotlin-reflect")
        exclude("org.jetbrains.kotlinx", "kotlinx-coroutines-core")
        exclude("org.slf4j", "slf4j-api")
    }
    api(libs.hikari)

    runtimeOnly("org.xerial:sqlite-jdbc:3.49.1.0")
    runtimeOnly("org.mariadb.jdbc:mariadb-java-client:3.5.2")
}

kotlin {
    jvmToolchain(21)
}

publishing {
    repositories {
        slneSnapshots()
    }
}