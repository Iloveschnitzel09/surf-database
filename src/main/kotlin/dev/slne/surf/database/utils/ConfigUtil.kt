package dev.slne.surf.database.utils

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.exists

object ConfigUtil {
    private val mapper = ObjectMapper(YAMLFactory())
        .findAndRegisterModules() // für Kotlin-Dataclasses, Zeittypen etc.

    fun <T> loadConfig(path: Path, clazz: Class<T>, default: T): T {
        return if (path.exists()) {
            Files.newBufferedReader(path).use {
                mapper.readValue(it, clazz)
            }
        } else {
            saveConfig(path, default)
            default
        }
    }

    fun <T> saveConfig(path: Path, config: T) {
        Files.createDirectories(path.parent)
        Files.newBufferedWriter(path).use {
            mapper.writeValue(it, config)
        }
    }
}
