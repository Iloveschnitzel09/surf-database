package dev.slne.surf.database.config

import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
class ExternalDatabaseConfig(
    val hostname: String? = "localhost",
    val port: Int? = 3306,
    val username: String? = "root",
    val password: String? = "",
    val database: String? = "database",
)