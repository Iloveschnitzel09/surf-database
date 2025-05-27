package dev.slne.surf.database.config

import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
data class DatabaseConfig (
    val storageMethod: String = "local",

    val local: LocalDatabaseConfig = LocalDatabaseConfig(),
    val external: ExternalDatabaseConfig = ExternalDatabaseConfig(),

    val hikari: DatabaseHikariConfig = DatabaseHikariConfig()
)