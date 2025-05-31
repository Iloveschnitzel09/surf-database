package dev.slne.surf.database.redis

import dev.slne.surf.database.config.ConnectionConfig
import dev.slne.surf.database.redis.listener.RedisEventHandler
import dev.slne.surf.database.redis.listener.RedisPacketEvent
import dev.slne.surf.database.redis.packet.RedisPacket
import dev.slne.surf.database.serializer.SurfSerializer
import dev.slne.surf.database.utils.callMethodWithRedisPacketEvent
import dev.slne.surf.database.utils.getAnnotatedMethods
import io.lettuce.core.*
import io.lettuce.core.api.StatefulRedisConnection
import io.lettuce.core.api.coroutines
import io.lettuce.core.api.coroutines.RedisStringCoroutinesCommands
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection
import io.lettuce.core.pubsub.api.async.RedisPubSubAsyncCommands
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.future.await
import kotlinx.coroutines.withContext
import kotlinx.serialization.PolymorphicSerializer
import kotlin.reflect.KFunction
import kotlin.time.DurationUnit
import kotlin.time.toDuration
import kotlin.time.toJavaDuration

@OptIn(ExperimentalLettuceCoroutinesApi::class)
class RedisProvider(private val connectionConfig: ConnectionConfig) {

    private var client: RedisClient? = null
    private var connection: StatefulRedisConnection<String, String>? = null
    private var pubSubConnection: StatefulRedisPubSubConnection<String, String>? = null
    private var _commands: RedisStringCoroutinesCommands<String, String>? = null
    private var _pubSub: RedisPubSubAsyncCommands<String, String>? = null

    val commands get() = _commands
    val pubSub get() = _pubSub

    private var _scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    val scope get() = _scope

    private val _listeners = mutableMapOf<Any, Set<KFunction<*>>>()
    val listeners = _listeners.toMap()

    suspend fun connect() = withContext(scope.coroutineContext) {
        if (client != null || connection != null || pubSubConnection != null) {
            error("Redis client is already initialized, disconnect first and try again.")
        }

        val redisConfig =
            connectionConfig.redis ?: throw IllegalStateException("Redis configuration is not set")

        val redisUri = RedisURI.builder()
            .withHost(redisConfig.host)
            .withPort(redisConfig.port)
            .withDatabase(redisConfig.database)
            .apply {
                redisConfig.password?.let { withPassword(it.toCharArray()) }
            }
            .withTimeout(
                redisConfig.connectTimeoutMillis
                    .toDuration(DurationUnit.MILLISECONDS)
                    .toJavaDuration()
            )
            .build()

        val clientOptions = ClientOptions.builder()
            .socketOptions(
                SocketOptions.builder()
                    .keepAlive(redisConfig.soKeepAlive)
                    .build()
            )
            .timeoutOptions(
                TimeoutOptions.builder()
                    .fixedTimeout(
                        redisConfig.readTimeoutSeconds
                            .toDuration(DurationUnit.SECONDS)
                            .toJavaDuration()
                    )
                    .build()
            )
            .autoReconnect(true)
            .build()

        client = RedisClient.create(redisUri)
        client!!.options = clientOptions

        connection = client!!.connect()
        pubSubConnection = client!!.connectPubSub()
        pubSubConnection!!.addListener(RedisSubscriberHandler(this@RedisProvider))

        _commands = connection!!.coroutines()
        _pubSub = pubSubConnection!!.async()

    }

    suspend inline fun <reified T : RedisPacket> publish(channel: String, packet: T): Long =
        withContext(scope.coroutineContext) {
            if (pubSub == null) {
                error("PubSub connection is not initialized, call connect() first.")
            }

            if (channel.isBlank()) {
                error("Channel cannot be blank.")
            }

            pubSub!!.publish(
                channel, SurfSerializer.json.encodeToString(
                    PolymorphicSerializer(RedisPacket::class),
                    packet
                )
            ).await()
        }

    suspend fun disconnect() = withContext(scope.coroutineContext) {
        connection?.closeAsync()?.await()
        pubSubConnection?.closeAsync()?.await()

        client?.shutdownAsync()?.await()

        client = null
        connection = null
        pubSubConnection = null
        _commands = null
        _pubSub = null
    }

    suspend fun subscribe(vararg channels: String) = withContext(scope.coroutineContext) {
        pubSub?.subscribe(*channels)
    }

    suspend fun unsubscribe(vararg channels: String) = withContext(scope.coroutineContext) {
        pubSub?.unsubscribe(*channels)
    }

    fun addListener(listener: Any) {
        if (_listeners.containsKey(listener)) {
            return
        }

        val annotatedMethods = getAnnotatedMethods<RedisEventHandler>(listener::class)

        if (annotatedMethods.isEmpty()) {
            return
        }

        _listeners[listener] = annotatedMethods.toSet()
    }

    fun removeListener(listener: Any) {
        _listeners.remove(listener)
    }

    fun callEvent(event: RedisPacketEvent) {
        _listeners.forEach { (listener, methods) ->
            methods.forEach { method ->
                callMethodWithRedisPacketEvent(listener, method, event)
            }
        }
    }
}