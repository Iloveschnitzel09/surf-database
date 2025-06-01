package dev.slne.surf.database

import dev.slne.surf.database.config.ConnectionConfig
import dev.slne.surf.database.database.DatabaseProvider
import dev.slne.surf.database.redis.RedisProvider
import dev.slne.surf.surfapi.core.api.config.createSpongeYmlConfig
import dev.slne.surf.surfapi.core.api.config.getSpongeConfig
import dev.slne.surf.surfapi.core.api.config.surfConfigApi
import java.nio.file.Path

class DatabaseManager(configDirectory: Path, storageDirectory: Path) {

    internal val connectionConfig
        get() = surfConfigApi.getSpongeConfig<ConnectionConfig>()

    val databaseProvider by lazy {
        DatabaseProvider(connectionConfig, storageDirectory)
    }
    
    val redisProvider by lazy {
        RedisProvider(connectionConfig)
    }

    init {
        surfConfigApi.createSpongeYmlConfig<ConnectionConfig>(
            configDirectory,
            "connection-config.yml"
        )
    }

}