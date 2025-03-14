package dev.slne.surf.database.config

import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
data class DatabaseHikariConfig(
    val minimumIdle: Int = 10,
    val maximumPoolSize: Int = 10,
    val idleTimeout: Long = 60000,
    val connectionTimeout: Long = 30000,
    val maxLifetime: Long = 1800000,
)
