package dev.slne.surf.database

import dev.slne.surf.database.config.DatabaseConfig
import dev.slne.surf.database.serializer.DatabaseSerializer
import dev.slne.surf.surfapi.core.api.config.createSpongeYmlConfig
import dev.slne.surf.surfapi.core.api.config.getSpongeConfig
import dev.slne.surf.surfapi.core.api.config.surfConfigApi
import dev.slne.surf.surfapi.core.api.util.logger
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import org.jetbrains.exposed.sql.Database
import java.nio.file.Path
import kotlin.io.path.*

class DatabaseProvider(configDirectory: Path, private val storageDirectory: Path) {

    private val log = logger()

    private val config: DatabaseConfig by lazy {
        var spongeConfig: DatabaseConfig = surfConfigApi.getSpongeConfig()

        if (spongeConfig == null) {
            spongeConfig = surfConfigApi.createSpongeYmlConfig(
                configDirectory,
                "database-config.yml"
            )
        }

        spongeConfig
    }

    val json = Json {
        ignoreUnknownKeys = true
        serializersModule = SerializersModule {
            DatabaseSerializer.register(this)
        }
    }

    suspend fun connect() {
        val method = config.storageMethod

        when (method.lowercase()) {
            "local" -> {
                val internal = config.local
                    ?: error("Local database config is null. Cannot connect to internal database.")
                val fileName = internal.fileName ?: "storage.db"

                Class.forName("org.sqlite.JDBC")
                val dbFile = storageDirectory / fileName

                if (dbFile.notExists()) {
                    dbFile.createFile()
                }

                Database.connect(
                    "jdbc:sqlite:file:${dbFile.absolutePathString()}",
                    "org.sqlite.JDBC"
                )

                log.atInfo().log("Successfully connected to database with sqlite!")
            }

            "external" -> {
                val external = config.external
                    ?: error("External database config is null. Cannot connect to external database.")

                val hostname = external.hostname ?: "localhost"
                val port = external.port ?: 3306
                val database = external.database ?: "database"
                val username = external.username ?: "root"
                val password = external.password ?: ""

                val url = "jdbc:mysql://$hostname:$port/$database"

                Class.forName("com.mysql.cj.jdbc.Driver")
                Database.connect(
                    url = url,
                    driver = "com.mysql.cj.jdbc.Driver",
                    user = username,
                    password = password
                )

                log.atInfo().log("Successfully connected to database with mysql!")
            }

            else -> {
                log.atWarning().log("Unknown storage method '%s'. Using local storage...", method)

                val internal = config.local
                    ?: error("Local database config is null. Cannot connect to internal database.")
                val fileName = internal.fileName ?: "storage.db"

                Class.forName("org.sqlite.JDBC")
                val dbFile = storageDirectory / fileName

                if (!dbFile.exists()) {
                    dbFile.createDirectories()
                    dbFile.createFile()
                }

                Database.connect(
                    "jdbc:sqlite:file:${dbFile.absolutePathString()}",
                    "org.sqlite.JDBC"
                )

                log.atInfo().log("Successfully connected to database with sqlite!")
            }
        }
    }
}