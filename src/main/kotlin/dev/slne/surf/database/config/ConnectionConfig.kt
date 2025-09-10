package dev.slne.surf.database.config

import dev.slne.surf.database.config.database.DatabaseConfig
import dev.slne.surf.database.config.redis.RedisConfig
import kotlinx.serialization.Serializable

@Serializable
internal data class ConnectionConfig(
    val database: DatabaseConfig = DatabaseConfig(),
    val redis: RedisConfig = RedisConfig()
)