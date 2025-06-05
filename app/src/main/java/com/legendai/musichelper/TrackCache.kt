package com.legendai.musichelper

import android.content.Context
import java.io.File
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

@Serializable
data class CachedTrack(
    val path: String,
    val createdAt: Long = System.currentTimeMillis()
)

object TrackCache {
    private const val FILE_NAME = "history.json"
    private val json = Json { prettyPrint = true }

    fun getTracks(context: Context): List<CachedTrack> {
        val file = File(context.filesDir, FILE_NAME)
        if (!file.exists()) return emptyList()
        return runCatching {
            json.decodeFromString(file.readText())
        }.getOrDefault(emptyList())
    }

    fun addTrack(context: Context, track: CachedTrack) {
        val tracks = getTracks(context).toMutableList()
        tracks.add(0, track)
        val file = File(context.filesDir, FILE_NAME)
        file.writeText(json.encodeToString(tracks))
    }
}
