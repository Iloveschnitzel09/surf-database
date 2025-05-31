package dev.slne.surf.database.redis.listener

import dev.slne.surf.database.redis.packet.RedisPacket

data class RedisPacketEvent(
    val channel: String,
    val packet: RedisPacket,
    val pattern: String? = null,
)
