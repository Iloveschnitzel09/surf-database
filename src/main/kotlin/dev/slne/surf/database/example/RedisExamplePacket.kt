package dev.slne.surf.database.example

import dev.slne.surf.database.redis.packet.RedisPacket
import kotlinx.serialization.Serializable

@Serializable
class RedisExamplePacket(val message: String) : RedisPacket() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is RedisExamplePacket) return false

        if (message != other.message) return false

        return true
    }

    override fun hashCode(): Int {
        return message.hashCode()
    }
}