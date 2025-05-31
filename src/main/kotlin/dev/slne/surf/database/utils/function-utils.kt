package dev.slne.surf.database.utils

import dev.slne.surf.database.redis.listener.RedisPacketEvent
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.valueParameters
import kotlin.reflect.jvm.jvmErasure

inline fun <reified K : Annotation> getAnnotatedMethods(clazz: KClass<*>) =
    clazz.declaredMemberFunctions.filter { it.findAnnotation<K>() != null }.filter {
        val params = it.valueParameters

        params.size == 1 && params[0].type.jvmErasure == RedisPacketEvent::class
    }

fun callMethodWithRedisPacketEvent(
    clazz: Any,
    function: KFunction<*>,
    event: RedisPacketEvent
) {
    try {
        function.call(clazz, event)
    } catch (exception: Exception) {
        System.err.println("Failed to call method ${function.name} in class ${clazz::class.simpleName} with RedisPacketEvent: ${exception.message ?: "Unknown error"}")
        exception.printStackTrace()
    }
}