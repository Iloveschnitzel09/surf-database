package dev.slne.surf.database.example

import dev.slne.surf.database.redis.packet.RedisPacket
import kotlinx.serialization.Serializable

@Serializable
class RedisExample2Packet(val message: String) : RedisPacket()