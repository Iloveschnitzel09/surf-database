package dev.slne.surf.database.user.minetools

import dev.slne.surf.database.user.UserLookupService
import java.util.*

object MinetoolsApiClient {

    private const val BASE_URL = "https://api.minetools.eu"

    /**
     * Get the username of a player by their UUID.
     *
     * @param uuid The UUID of the player.
     *
     * @return The username of the player, or null if the player does not exist.
     */
    suspend fun getUsername(uuid: UUID): MinetoolsApiResponse? =
        UserLookupService.fetchFromApi("$BASE_URL/uuid/$uuid")

    /**
     * Get the UUID of a player by their username.
     *
     * @param username The username of the player.
     *
     * @return The UUID of the player, or null if the player does not exist.
     */
    suspend fun getUuid(username: String): MinetoolsApiResponse? =
        UserLookupService.fetchFromApi("$BASE_URL/uuid/$username")
}