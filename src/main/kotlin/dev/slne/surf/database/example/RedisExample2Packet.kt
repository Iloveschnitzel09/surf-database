package dev.slne.surf.database.example

import dev.slne.surf.database.redis.packet.RedisPacket
import kotlinx.serialization.Serializable

@Serializable
class RedisExample2Packet(val message: String) : RedisPacket() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is RedisExample2Packet) return false

        if (message != other.message) return false

        return true
    }

    override fun hashCode(): Int {
        return message.hashCode()
    }
}