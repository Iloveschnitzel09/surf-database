package dev.slne.surf.database.user.minetools

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.util.*

/**
 * {
 *   "cache": {
 *     "HIT": false,
 *     "cache_time": 518400,
 *     "cache_time_left": 518399,
 *     "cached_at": 1736343742.7480717,
 *     "cached_until": 1736862142.7480717
 *   },
 *   "id": "5c63e51b82b14222af0f66a4c31e36ad",
 *   "name": "NotAmmo",
 *   "status": "OK"
 * }
 */
@Serializable
data class MinetoolsApiResponse(
    @SerialName("id")
    val id: String,

    @SerialName("name")
    val name: String,

    @SerialName("status")
    val status: String,

    @SerialName("cache")
    val cache: MinetoolsApiResponseCache,
) {

    @Transient
    val uuid = UUID.fromString(
        id.substring(0, 8) + "-" +
                id.substring(8, 12) + "-" +
                id.substring(12, 16) + "-" +
                id.substring(16, 20) + "-" +
                id.substring(20)
    )

    /**
     * "cache": {
     *     "HIT": false,
     *     "cache_time": 518400,
     *     "cache_time_left": 518399,
     *     "cached_at": 1736343742.7480717,
     *     "cached_until": 1736862142.7480717
     *   }
     */
    @Serializable
    data class MinetoolsApiResponseCache(
        @SerialName("HIT")
        val hit: Boolean,

        @SerialName("cache_time")
        val cacheTime: Long,

        @SerialName("cache_time_left")
        val cacheTimeLeft: Long?,

        @SerialName("cached_at")
        val cachedAt: Double,

        @SerialName("cached_until")
        val cachedUntil: Double,
    )
}