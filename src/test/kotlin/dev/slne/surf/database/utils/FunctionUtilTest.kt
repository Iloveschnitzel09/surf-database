package dev.slne.surf.database.utils

import dev.slne.surf.database.redis.listener.RedisEventHandler
import dev.slne.surf.database.redis.listener.RedisPacketEvent
import kotlin.test.Test
import kotlin.test.assertEquals

class FunctionUtilTest {

    class TestHandler {
        @RedisEventHandler
        suspend fun valid1(packet: RedisPacketEvent) {
        }

        @RedisEventHandler
        fun valid2(packet: RedisPacketEvent) {
        }

        suspend fun invalid1(packet: String) {}

        suspend fun invalid2(packet: RedisPacketEvent, extra: String) {

        }

        suspend fun invalid3() {}

        @RedisEventHandler
        suspend fun invalid4(packet: String) {
        }

        @RedisEventHandler
        suspend fun invalid5(packet: RedisPacketEvent, extra: String) {
        }

        @RedisEventHandler
        suspend fun invalid6() {
        }
    }

    @Test
    fun `find all RedisPacketEvent functions`() {
        val functions = getAnnotatedMethods<RedisEventHandler>(TestHandler::class)
        val expected = listOf(
            "valid1",
            "valid2"
        )
        val actual = functions.map { it.name }

        assertEquals(expected.sorted(), actual.sorted())
    }
}