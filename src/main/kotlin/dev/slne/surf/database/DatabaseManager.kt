package dev.slne.surf.database

import dev.slne.surf.database.config.ConnectionConfig
import dev.slne.surf.database.database.DatabaseProvider
import dev.slne.surf.database.redis.RedisProvider
import dev.slne.surf.surfapi.core.api.config.createSpongeYmlConfig
import dev.slne.surf.surfapi.core.api.config.getSpongeConfig
import dev.slne.surf.surfapi.core.api.config.surfConfigApi
import java.nio.file.Path

class DatabaseManager(private val configDirectory: Path, private val storageDirectory: Path) {

    internal val connectionConfig = surfConfigApi.getSpongeConfig<ConnectionConfig>()

    val databaseProvider = DatabaseProvider(connectionConfig, storageDirectory)
    val redisProvider = RedisProvider(connectionConfig)

    init {
        surfConfigApi.createSpongeYmlConfig<ConnectionConfig>(
            configDirectory,
            "connection-config.yml"
        )
    }

}