package dev.slne.surf.database.example

import dev.slne.surf.database.redis.packet.RedisPacket
import kotlinx.serialization.Serializable

@Serializable
internal class RedisExamplePacket(val message: String) : RedisPacket()