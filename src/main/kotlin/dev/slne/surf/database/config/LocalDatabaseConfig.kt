package dev.slne.surf.database.config

import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
data class LocalDatabaseConfig(
    val fileName: String? = "storage.db"
)