package dev.slne.surf.database.user.minecraft

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.util.*

/**
 * {
 *   "id": "5c63e51b82b14222af0f66a4c31e36ad",
 *   "name": "NotAmmo"
 * }
 */
@Serializable
data class MinecraftApiResponse(
    @SerialName("id")
    val id: String,

    @SerialName("name")
    val name: String,
) {

    @Transient
    val uuid = UUID.fromString(
        id.substring(0, 8) + "-" +
                id.substring(8, 12) + "-" +
                id.substring(12, 16) + "-" +
                id.substring(16, 20) + "-" +
                id.substring(20)
    )
}