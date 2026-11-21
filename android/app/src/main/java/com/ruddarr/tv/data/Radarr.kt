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
data class MovieImage(val coverType: String, val remoteUrl: String? = null)

@Serializable
data class Movie(
    val id: Int = 0,
    val title: String,
    val sortTitle: String = "",
    val year: Int = 0,
    val runtime: Int = 0,
    val overview: String? = null,
    val studio: String? = null,
    val certification: String? = null,
    val genres: List<String> = emptyList(),
    val monitored: Boolean = false,
    val hasFile: Boolean = false,
    val sizeOnDisk: Long = 0,
    val images: List<MovieImage> = emptyList(),
) {
    val posterUrl: String? get() = images.firstOrNull { it.coverType == "poster" }?.remoteUrl
    val fanartUrl: String? get() = images.firstOrNull { it.coverType == "fanart" }?.remoteUrl
}

/** Radarr instance, stored on-device. */
data class Instance(val url: String, val apiKey: String)

class InstanceStore(context: Context) {
    private val prefs = context.getSharedPreferences("instance", Context.MODE_PRIVATE)

    fun load(): Instance? {
        val url = prefs.getString("url", null) ?: return null
        val key = prefs.getString("apiKey", null) ?: return null
        return Instance(url, key)
    }

    fun save(instance: Instance) {
        prefs.edit().putString("url", instance.url).putString("apiKey", instance.apiKey).apply()
    }

    fun clear() = prefs.edit().clear().apply()
}

class RadarrApi(private val instance: Instance) {
    private val json = Json { ignoreUnknownKeys = true; coerceInputValues = true }

    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun movies(): List<Movie> = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url(instance.url.trimEnd('/') + "/api/v3/movie")
            .header("X-Api-Key", instance.apiKey)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) error("HTTP ${response.code}")
            json.decodeFromString<List<Movie>>(response.body!!.string())
        }
    }
}
