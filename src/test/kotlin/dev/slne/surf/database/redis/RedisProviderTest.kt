package dev.slne.surf.database.redis

import dev.slne.surf.database.config.ConnectionConfig
import dev.slne.surf.database.config.redis.RedisConfig
import dev.slne.surf.database.example.RedisExample2Packet
import dev.slne.surf.database.example.RedisExamplePacket
import dev.slne.surf.database.redis.listener.RedisEventHandler
import dev.slne.surf.database.redis.listener.RedisPacketEvent
import io.lettuce.core.ExperimentalLettuceCoroutinesApi
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.jupiter.api.*
import kotlin.test.Test
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@OptIn(ExperimentalLettuceCoroutinesApi::class)
class RedisProviderTest {
    private lateinit var provider: RedisProvider
    private val channel = "test-channel"

    @BeforeAll
    fun setup() {
        provider = RedisProvider(
            ConnectionConfig(
                redis = RedisConfig(
                    host = System.getenv("REDIS_HOST") ?: "localhost",
                    port = System.getenv("REDIS_PORT")?.toIntOrNull() ?: 6379,
                    password = System.getenv("REDIS_PASSWORD") ?: "",
                    database = System.getenv("REDIS_DB")?.toIntOrNull() ?: 0,
                )
            )
        )
        runBlocking {
            provider.connect()
            provider.subscribe(channel)
        }
    }

    @AfterAll
    fun tearDown() {
        runBlocking {
            provider.disconnect()
        }
    }

    @Test
    @Order(1)
    fun `should set and get a value from redis`() = runBlocking {
        val key = "test-key"
        val value = "test-value"

        provider.commands?.set(key, value)
        val result = provider.commands?.get(key)

        assertEquals(value, result)
    }

    @Test
    @Order(2)
    fun `should delete a value from redis`() = runBlocking {
        val key = "test-key"

        provider.commands?.getdel(key)
        val result = provider.commands?.get(key)

        assertEquals(null, result)
    }

    @Test
    @Order(3)
    fun `should handle non-existing key gracefully`() = runBlocking {
        val key = "non-existing-key"
        val result = provider.commands?.get(key)

        assertEquals(null, result)
    }

    @Test
    @Order(4)
    fun `should handle connection errors gracefully`() = runBlocking {
        val faultyProvider = RedisProvider(
            ConnectionConfig(
                redis = RedisConfig(
                    host = "invalid-host",
                    port = 6379,
                    password = "",
                    database = 0,
                )
            )
        )

        try {
            faultyProvider.connect()
            Assertions.fail("Expected an exception to be thrown")
        } catch (e: Exception) {
            // Expected exception
        } finally {
            faultyProvider.disconnect()
        }
    }

    @Test
    @Order(5)
    fun `should handle empty key gracefully`(): Unit = runBlocking {
        val key = ""
        val value = "test-value"

        provider.commands?.set(key, value)
        val result = provider.commands?.get(key)

        assertEquals(value, result)

        // Clean up
        provider.commands?.getdel(key)
    }

    private class TestPubSubListener(val toComplete: CompletableDeferred<RedisExamplePacket>) {
        @RedisEventHandler
        fun onMessage(event: RedisPacketEvent) {
            if (event.packet !is RedisExamplePacket) return

            toComplete.complete(event.packet)
        }
    }

    private class Test2PubSubListener(val toComplete: CompletableDeferred<RedisExamplePacket>) {
        @RedisEventHandler
        fun onMessage(event: RedisPacketEvent) {
            if (event.packet !is RedisExamplePacket) return

            toComplete.complete(event.packet)
        }
    }


    @Test
    @Order(6)
    fun `should send and receive pubsub messages`() = runBlocking {
        val messageContent = "Hello, Redis!"
        val packet = RedisExamplePacket(messageContent)

        val toComplete = CompletableDeferred<RedisExamplePacket>()
        provider.addListener(TestPubSubListener(toComplete))

        provider.publish(channel, packet)

        val receivedPacket = withTimeout(5000) {
            toComplete.await()
        }

        assertEquals(packet.message, receivedPacket.message)
    }

    @Test
    @Order(7)
    fun `should not call handler for unregistered packet type`() = runBlocking {
        val wrongPacket = RedisExample2Packet("This should not be handled")

        val toComplete = CompletableDeferred<RedisExamplePacket>()
        provider.addListener(Test2PubSubListener(toComplete))

        provider.publish(channel, wrongPacket)

        // Sollte nicht completed werden, da kein passender Handler existiert
        try {
            withTimeout(3000) {
                toComplete.await()
            }
            Assertions.fail("Handler was incorrectly called for an unregistered packet type.")
        } catch (exception: TimeoutCancellationException) {

        }
    }

}