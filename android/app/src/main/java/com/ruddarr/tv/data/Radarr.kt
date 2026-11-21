package com.ruddarr.tv.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

@Serializable
data class MediaImage(val coverType: String, val remoteUrl: String? = null)

enum class Kind(val label: String) { Radarr("Movies"), Sonarr("Series") }

/** Common shape the library and detail screens render, for both Radarr and Sonarr. */
interface Media {
    val id: Int
    val title: String
    val sortTitle: String
    val year: Int
    val overview: String?
    val posterUrl: String?
    /** Short fact lines shown on the detail screen. */
    val facts: List<String>
}

@Serializable
data class Movie(
    override val id: Int = 0,
    override val title: String,
    override val sortTitle: String = "",
    override val year: Int = 0,
    val runtime: Int = 0,
    override val overview: String? = null,
    val studio: String? = null,
    val certification: String? = null,
    val genres: List<String> = emptyList(),
    val monitored: Boolean = false,
    val hasFile: Boolean = false,
    val sizeOnDisk: Long = 0,
    val images: List<MediaImage> = emptyList(),
) : Media {
    override val posterUrl: String? get() = images.firstOrNull { it.coverType == "poster" }?.remoteUrl
    override val facts: List<String> get() = listOfNotNull(
        listOfNotNull(certification, if (runtime > 0) "$runtime min" else null, genres.takeIf { it.isNotEmpty() }?.joinToString(", "))
            .joinToString(" · ").ifEmpty { null },
        if (hasFile) "Downloaded" else if (monitored) "Missing (monitored)" else "Unmonitored",
    )
}

@Serializable
data class SeriesStatistics(
    val seasonCount: Int = 0,
    val episodeFileCount: Int = 0,
    val episodeCount: Int = 0,
    val sizeOnDisk: Long = 0,
)

@Serializable
data class Series(
    override val id: Int = 0,
    override val title: String,
    override val sortTitle: String = "",
    override val year: Int = 0,
    val runtime: Int = 0,
    override val overview: String? = null,
    val network: String? = null,
    val status: String? = null,
    val certification: String? = null,
    val genres: List<String> = emptyList(),
    val monitored: Boolean = false,
    val statistics: SeriesStatistics = SeriesStatistics(),
    val images: List<MediaImage> = emptyList(),
) : Media {
    override val posterUrl: String? get() = images.firstOrNull { it.coverType == "poster" }?.remoteUrl
    override val facts: List<String> get() = listOfNotNull(
        listOfNotNull(network, certification, status?.replaceFirstChar { it.uppercase() },
            genres.takeIf { it.isNotEmpty() }?.joinToString(", ")).joinToString(" · ").ifEmpty { null },
        "${statistics.seasonCount} seasons · ${statistics.episodeFileCount}/${statistics.episodeCount} episodes",
        if (monitored) "Monitored" else "Unmonitored",
    )
}

/** Radarr or Sonarr instance, stored on-device. */
data class Instance(val url: String, val apiKey: String)

class InstanceStore(context: Context) {
    private val prefs = context.getSharedPreferences("instance", Context.MODE_PRIVATE)

    fun load(kind: Kind): Instance? {
        val url = prefs.getString("${kind.name}.url", null) ?: return null
        val key = prefs.getString("${kind.name}.apiKey", null) ?: return null
        return Instance(url, key)
    }

    fun save(kind: Kind, instance: Instance) {
        prefs.edit().putString("${kind.name}.url", instance.url).putString("${kind.name}.apiKey", instance.apiKey).apply()
    }
}

/** Radarr and Sonarr share the v3 API shape. */
class ArrApi(private val instance: Instance) {
    private val json = Json { ignoreUnknownKeys = true; coerceInputValues = true }

    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun movies(): List<Movie> = get("movie") { json.decodeFromString(it) }

    suspend fun series(): List<Series> = get("series") { json.decodeFromString(it) }

    private suspend fun <T> get(path: String, decode: (String) -> T): T = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url(instance.url.trimEnd('/') + "/api/v3/" + path)
            .header("X-Api-Key", instance.apiKey)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) error("HTTP ${response.code}")
            decode(response.body!!.string())
        }
    }
}
