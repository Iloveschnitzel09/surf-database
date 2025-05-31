package dev.slne.surf.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import dev.slne.surf.database.config.DatabaseConfig
import dev.slne.surf.database.config.DatabaseHikariConfig
import dev.slne.surf.surfapi.core.api.config.createSpongeYmlConfig
import dev.slne.surf.surfapi.core.api.config.getSpongeConfig
import dev.slne.surf.surfapi.core.api.config.surfConfigApi
import dev.slne.surf.surfapi.core.api.util.logger
import org.jetbrains.exposed.sql.Database
import java.nio.file.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.createFile
import kotlin.io.path.div
import kotlin.io.path.notExists

class DatabaseProvider(configDirectory: Path, private val storageDirectory: Path) {

    private val log = logger()
    private lateinit var connection: Database

    init {
        surfConfigApi.createSpongeYmlConfig<DatabaseConfig>(
            configDirectory,
            "database-config.yml"
        )
    }

    private val config get() = surfConfigApi.getSpongeConfig<DatabaseConfig>()

    fun connect() {
        if (::connection.isInitialized && !connection.connector().isClosed) {
            disconnect()
        }

        when (config.storageMethod.lowercase()) {
            "local" -> {
                connectLocal()
            }

            "external" -> {
                connectExternal()
            }

            else -> {
                log.atWarning().log(
                    "Unknown storage method '%s'. Using local storage...",
                    config.storageMethod
                )
                connectLocal()
            }
        }
    }

    private fun connectLocal() {
        val internal = config.local
        val fileName = internal.fileName ?: "storage.db"

        Class.forName("org.sqlite.JDBC")
        val dbFile = storageDirectory / fileName

        if (dbFile.notExists()) {
            dbFile.createFile()
        }

        connectUsingHikari(
            "sqlite",
            "org.sqlite.JDBC",
            "",
            0,
            "",
            "",
            "",
            config.hikari,
            "jdbc:sqlite:file:${dbFile.absolutePathString()}",
        )

        log.atInfo().log("Successfully connected to database with sqlite!")
    }

    private fun connectExternal() {
        val external = config.external
        val hikari = config.hikari

        connectUsingHikari(
            external.connector,
            external.driver,
            external.hostname,
            external.port,
            external.database,
            external.username,
            external.password,
            hikari
        )

        log.atInfo().log("Successfully connected to database with mysql!")
    }

    private fun connectUsingHikari(
        connector: String,
        driver: String,
        hostname: String,
        port: Int,
        database: String,
        username: String,
        password: String,
        hikari: DatabaseHikariConfig,
        url: String = "jdbc:${connector}://${hostname}:${port}/${database}"
    ) {
        Class.forName(driver)

        val hikariConfig = HikariConfig().apply {
            this.jdbcUrl = url
            this.username = username
            this.password = password
            this.minimumIdle = hikari.minimumIdle
            this.maximumPoolSize = hikari.maximumPoolSize
            this.idleTimeout = hikari.idleTimeout
            this.connectionTimeout = hikari.connectionTimeout
            this.maxLifetime = hikari.maxLifetime
            this.driverClassName = driver
            this.isAutoCommit = false
            this.transactionIsolation = "TRANSACTION_REPEATABLE_READ"

            validate()
        }

        connection = Database.connect(HikariDataSource(hikariConfig))
    }

    fun disconnect() {
        if (!::connection.isInitialized || connection.connector().isClosed) {
            return
        }

        connection.connector().close()
    }
}