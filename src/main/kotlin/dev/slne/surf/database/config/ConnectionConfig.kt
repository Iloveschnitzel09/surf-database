package dev.slne.surf.database.config

import dev.slne.surf.database.config.database.DatabaseConfig
import dev.slne.surf.database.config.redis.RedisConfig
import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
data class ConnectionConfig(
    val database: DatabaseConfig? = DatabaseConfig(),
    val redis: RedisConfig? = RedisConfig()
)