package dev.slne.surf.database.redis

import dev.slne.surf.database.redis.listener.RedisPacketEvent
import dev.slne.surf.database.redis.packet.RedisPacket
import io.lettuce.core.pubsub.RedisPubSubListener

internal class RedisSubscriberHandler(private val redisProvider: RedisProvider) :
    RedisPubSubListener<String, String> {

    override fun message(channel: String, message: String) {
        val packet = RedisPacket.deserialize(message)
        val event = RedisPacketEvent(channel, packet)

        redisProvider.callEvent(event)
    }

    override fun message(pattern: String, channel: String, message: String) {
        val packet = RedisPacket.deserialize(message)
        val event = RedisPacketEvent(channel, packet, pattern)

        redisProvider.callEvent(event)
    }

    override fun subscribed(channel: String, count: Long) {
        println("Subscribed to channel: $channel, total subscribed channels: $count")
    }

    override fun psubscribed(pattern: String, count: Long) {
        println("Subscribed to pattern: $pattern, total subscribed patterns: $count")
    }

    override fun unsubscribed(channel: String, count: Long) {
        println("Unsubscribed from channel: $channel, total subscribed channels: $count")
    }

    override fun punsubscribed(pattern: String, count: Long) {
        println("Unsubscribed from pattern: $pattern, total subscribed patterns: $count")
    }
}