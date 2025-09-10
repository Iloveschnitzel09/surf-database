package dev.slne.surf.database.config.database

import dev.slne.surf.database.database.DatabaseStorageMethod
import kotlinx.serialization.Serializable

@Serializable
internal data class DatabaseConfig(
    val storageMethod: DatabaseStorageMethod = DatabaseStorageMethod.LOCAL,

    val local: LocalDatabaseConfig = LocalDatabaseConfig(),
    val external: ExternalDatabaseConfig = ExternalDatabaseConfig(),

    val hikari: DatabaseHikariConfig = DatabaseHikariConfig()
)