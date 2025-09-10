package dev.slne.surf.database

import dev.slne.surf.database.config.ConnectionConfig
import dev.slne.surf.database.database.DatabaseProvider
import dev.slne.surf.database.redis.RedisProvider
import dev.slne.surf.database.utils.ConfigUtil
import java.nio.file.Path

class DatabaseManager(configDirectory: Path, storageDirectory: Path) {

    internal val connectionConfig: ConnectionConfig
    val databaseProvider: DatabaseProvider
    val redisProvider: RedisProvider

    init {
        val configPath = configDirectory.resolve("connection-config.yml")
        connectionConfig = ConfigUtil.loadConfig(configPath, ConnectionConfig::class.java, ConnectionConfig())
        databaseProvider = DatabaseProvider(connectionConfig, storageDirectory)
        redisProvider = RedisProvider(connectionConfig)
    }
}
