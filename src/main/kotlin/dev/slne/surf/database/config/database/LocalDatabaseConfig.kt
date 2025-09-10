package dev.slne.surf.database.config.database

import kotlinx.serialization.Serializable

@Serializable
internal data class LocalDatabaseConfig(
    val fileName: String = "storage.db"
)