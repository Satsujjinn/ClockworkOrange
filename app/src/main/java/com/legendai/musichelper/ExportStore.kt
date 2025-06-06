package com.legendai.musichelper

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

@Serializable
private data class ExportEntries(val entries: List<ExportStore.Entry> = emptyList())

private object ExportEntriesSerializer : Serializer<ExportEntries> {
    override val defaultValue: ExportEntries = ExportEntries()

    override suspend fun readFrom(input: InputStream): ExportEntries {
        return try {
            Json.decodeFromString(ExportEntries.serializer(), input.readBytes().decodeToString())
        } catch (e: Exception) {
            defaultValue
        }
    }

    override suspend fun writeTo(t: ExportEntries, output: OutputStream) {
        output.write(Json.encodeToString(ExportEntries.serializer(), t).toByteArray())
    }
}

private val Context.exportsDataStore: DataStore<ExportEntries> by dataStore(
    fileName = "exports.json",
    serializer = ExportEntriesSerializer
)

/**
 * Helper for tracking exported files using DataStore.
 */
object ExportStore {
    @Serializable
    data class Entry(val fileName: String, val time: Long)

    fun flow(context: Context): Flow<List<Entry>> =
        context.exportsDataStore.data.map { it.entries.sortedByDescending { e -> e.time } }

    suspend fun add(context: Context, fileName: String) {
        context.exportsDataStore.updateData { current ->
            current.copy(entries = current.entries + Entry(fileName, System.currentTimeMillis()))
        }
    }

    suspend fun list(context: Context): List<Entry> =
        context.exportsDataStore.data.first().entries.sortedByDescending { it.time }

    suspend fun remove(context: Context, fileName: String) {
        context.exportsDataStore.updateData { current ->
            current.copy(entries = current.entries.filterNot { it.fileName == fileName })
        }
    }
}
