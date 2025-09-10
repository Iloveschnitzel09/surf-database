plugins {
    kotlin("jvm") version "2.2.0"
    kotlin("plugin.serialization") version "2.0.0"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}


group = "dev.slne.surf"
version = findProperty("version") as String

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.bundles.exposed){
        exclude("org.jetbrains.kotlin", "kotlin-stdlib")
        exclude("org.jetbrains.kotlin", "kotlin-reflect")
        exclude("org.slf4j", "slf4j-api")
    }
    implementation(libs.hikari)
    implementation(libs.lettuce)

    runtimeOnly(libs.sqlite)
    runtimeOnly(libs.mariadb)

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.18.2")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.18.2")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.7.3")
}

kotlin {
    jvmToolchain(21)
}