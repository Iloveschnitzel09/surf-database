package dev.slne.surf.database.user

import com.github.benmanes.caffeine.cache.Caffeine
import com.sksamuel.aedile.core.expireAfterWrite
import dev.slne.surf.database.user.minecraft.MinecraftApiClient
import dev.slne.surf.database.user.minetools.MinetoolsApiClient
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.future.await
import kotlinx.coroutines.future.future
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.util.*
import kotlin.coroutines.CoroutineContext
import kotlin.time.Duration.Companion.hours

@OptIn(DelicateCoroutinesApi::class)
object UserLookupService {

    private val nameToUuidCache = Caffeine.newBuilder()
        .expireAfterWrite(1.hours)
        .buildAsync<String, UUID> { key, _ ->
            GlobalScope.future {
                try {
                    MinecraftApiClient.getUuid(key)?.uuid
                } catch (_: Exception) {
                    try {
                        MinetoolsApiClient.getUuid(key)?.uuid
                    } catch (_: Exception) {
                        null
                    }
                }
            }
        }

    private val uuidToNameCache = Caffeine.newBuilder()
        .expireAfterWrite(1.hours)
        .buildAsync<UUID, String> { key, _ ->
            GlobalScope.future {
                try {
                    MinecraftApiClient.getUsername(key)?.name
                } catch (_: Exception) {
                    try {
                        MinetoolsApiClient.getUsername(key)?.name
                    } catch (_: Exception) {
                        null
                    }
                }
            }
        }

    val client = HttpClient()
    val json = Json { ignoreUnknownKeys = true }

    /**
     * Fetches data from an API and deserializes it into an object of type [T].
     *
     * @param url The URL of the API endpoint.
     *
     * @return The deserialized object, or null if the request failed.
     */
    suspend inline fun <reified T> fetchFromApi(url: String): T? {
        val response: HttpResponse = client.get(url)

        return if (response.status.value == 200) {
            json.decodeFromString<T>(response.body())
        } else {
            null
        }
    }

    /**
     * Returns the UUID of a player by their username.
     *
     * @param username The username of the player.
     *
     * @return The UUID of the player, or null if the player does not exist.
     */
    suspend fun getUuidByUsername(
        username: String,
        context: CoroutineContext = Dispatchers.IO
    ): UUID? = withContext(context) { nameToUuidCache.get(username).await() }

    /**
     * Returns the username of a player by their UUID.
     *
     * @param uuid The UUID of the player.
     *
     * @return The username of the player, or null if the player does not exist.
     */
    suspend fun getUsernameByUuid(
        uuid: UUID,
        context: CoroutineContext = Dispatchers.IO
    ): String? = withContext(context) { uuidToNameCache.get(uuid).await() }
}