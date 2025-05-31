package dev.slne.surf.database.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import dev.slne.surf.database.config.ConnectionConfig
import dev.slne.surf.database.config.database.DatabaseHikariConfig
import org.jetbrains.exposed.sql.Database
import java.nio.file.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.createFile
import kotlin.io.path.div
import kotlin.io.path.notExists

class DatabaseProvider(
    private val connectionConfig: ConnectionConfig,
    private val storageDirectory: Path
) {

    private var connection: Database? = null
    private var dataSource: HikariDataSource? = null

    fun connect() {
        val database = connectionConfig.database
            ?: error("Database configuration is missing for connect")

        connection?.connector()?.isClosed?.let {
            if (!it) {
                disconnect()
            }
        }

        when (database.storageMethod.lowercase()) {
            "local" -> {
                connectLocal()
            }

            "external" -> {
                connectExternal()
            }

            else -> {
                System.err.println("Unknown storage method '${database.storageMethod}'. Using local storage...")
                connectLocal()
            }
        }
    }

    private fun connectLocal() {
        val database = connectionConfig.database
            ?: error("Database configuration is missing for local storage")
        val local = database.local ?: error("Local storage not configured")
        val fileName = local.fileName ?: "storage.db"

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
            database.hikari,
            "jdbc:sqlite:file:${dbFile.absolutePathString()}",
        )

        println("Successfully connected to database with sqlite! File: ${dbFile.absolutePathString()}")
    }

    private fun connectExternal() {
        val database = connectionConfig.database
            ?: error("Database configuration is missing for external storage")
        val external = database.external ?: error("External storage not configured")

        connectUsingHikari(
            external.connector,
            external.driver,
            external.hostname,
            external.port,
            external.database,
            external.username,
            external.password,
            database.hikari
        )

        println("Successfully connected to database with ${external.connector} at ${external.hostname}:${external.port}/${external.database}")
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

        dataSource = HikariDataSource(hikariConfig)
        connection = Database.Companion.connect(dataSource!!)
    }

    fun disconnect() {
        if (connection?.connector()?.isClosed == true) {
            System.err.println("Database connection is not initialized or already closed.")
            return
        }

        dataSource?.takeIf { !it.isClosed }?.close()
        connection = null
        dataSource = null
    }
}