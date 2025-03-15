package dev.slne.surf.database.user.minecraft

import dev.slne.surf.database.user.UserLookupService
import java.util.*

object MinecraftApiClient {

    private const val BASE_URL = "https://api.mojang.com"

    /**
     * Get the username of a player by their UUID.
     *
     * @param uuid The UUID of the player.
     *
     * @return The username of the player, or null if the player does not exist.
     */
    suspend fun getUsername(uuid: UUID): MinecraftApiResponse? =
        UserLookupService.fetchFromApi("$BASE_URL/user/profile/$uuid")

    /**
     * Get the UUID of a player by their username.
     *
     * @param username The username of the player.
     *
     * @return The UUID of the player, or null if the player does not exist.
     */
    suspend fun getUuid(username: String): MinecraftApiResponse? =
        UserLookupService.fetchFromApi("$BASE_URL/users/profiles/minecraft/$username")
}
