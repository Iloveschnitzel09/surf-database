package dev.slne.surf.database.config.redis

import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
data class RedisConfig(
    val host: String = "localhost",
    val port: Int = 6379,
    val password: String? = "",
    val database: Int = 0,

    val connectTimeoutMillis: Int = 5000,
    val readTimeoutSeconds: Int = 5,
    val soKeepAlive: Boolean = true,
)
