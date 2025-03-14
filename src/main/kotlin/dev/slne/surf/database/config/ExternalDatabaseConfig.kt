package dev.slne.surf.database.config

import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
data class ExternalDatabaseConfig(
    val connector: String = "mariadb",
    val driver: String = "org.mariadb.jdbc.Driver",

    val hostname: String = "localhost",
    val port: Int = 3306,
    val username: String = "root",
    val password: String = "",
    val database: String = "database",
)