package dev.slne.surf.database.serializer

import dev.slne.surf.database.example.RedisExample2Packet
import dev.slne.surf.database.example.RedisExamplePacket
import dev.slne.surf.database.redis.packet.RedisPacket
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.SerializersModuleBuilder
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

object SurfSerializer {

    private val baseModule = SerializersModule {
        register(this)

        polymorphic(RedisPacket::class) {
            subclass(RedisExamplePacket::class)
            subclass(RedisExample2Packet::class)
        }
    }

    private val externalModules = mutableSetOf<SerializersModule>()

    fun register(builder: SerializersModuleBuilder) = with(builder) {
    }

    private var _json: Json = buildJson()
    val json get() = _json

    private fun buildJson(): Json {
        val mergedModules = SerializersModule {
            include(baseModule)
            externalModules.forEach { include(it) }
        }

        return Json {
            serializersModule = mergedModules
            classDiscriminator = "type"
            ignoreUnknownKeys = true
            encodeDefaults = true
            isLenient = true
        }
    }

    private fun rebuildJson() {
        _json = buildJson()
    }

    fun registerModule(module: SerializersModule) {
        externalModules.add(module)
        rebuildJson()
    }

}