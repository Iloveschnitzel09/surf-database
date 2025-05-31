package dev.slne.surf.database.database

import dev.slne.surf.database.config.ConnectionConfig
import dev.slne.surf.database.config.database.DatabaseConfig
import dev.slne.surf.database.config.database.DatabaseHikariConfig
import dev.slne.surf.database.config.database.LocalDatabaseConfig
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.*
import java.nio.file.Files
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class LocalDatabaseProviderTest {

    private lateinit var provider: DatabaseProvider
    private val tempDir = Files.createTempDirectory("db-test")

    @BeforeAll
    fun setup() {
        val config = ConnectionConfig(
            database = DatabaseConfig(
                storageMethod = DatabaseStorageMethod.LOCAL,
                local = LocalDatabaseConfig("test.db"),
                hikari = DatabaseHikariConfig()
            )
        )

        provider = DatabaseProvider(config, tempDir)
        provider.connect()
    }

    @AfterAll
    fun cleanup() {
        provider.disconnect()
    }

    private object Users : IntIdTable("local_users") {
        val name = varchar("name", 50)
    }

    @Test
    @Order(1)
    fun `should connect and create table`() {
        transaction {
            SchemaUtils.create(Users)
        }
    }

    @Test
    @Order(2)
    fun `should create model`() {
        assertDoesNotThrow {
            transaction {
                Users.insert {
                    it[name] = "Alice"
                }

                val user = Users.selectAll().firstOrNull()
                assertEquals("Alice", user?.get(Users.name))
            }
        }
    }

    @Test
    @Order(3)
    fun `should disconnect without exception`() {
        provider.disconnect()
        assertDoesNotThrow { provider.disconnect() }
    }

    @Test
    @Order(4)
    fun `should reconnect without error`() {
        assertDoesNotThrow {
            provider.connect()
        }
    }
}
