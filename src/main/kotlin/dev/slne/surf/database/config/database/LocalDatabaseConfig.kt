package dev.slne.surf.database.config.database

import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
internal data class LocalDatabaseConfig(
    val fileName: String = "storage.db"
)