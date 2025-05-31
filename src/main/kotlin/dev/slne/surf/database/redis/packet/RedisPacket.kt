package dev.slne.surf.database.redis.packet

import dev.slne.surf.database.serializer.SurfSerializer
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.PolymorphicSerializer
import kotlinx.serialization.Serializable

@Serializable
@Polymorphic
open class RedisPacket {
    companion object {
        fun deserialize(jsonStr: String): RedisPacket =
            SurfSerializer.json.decodeFromString(
                PolymorphicSerializer(RedisPacket::class),
                jsonStr
            )
    }
}