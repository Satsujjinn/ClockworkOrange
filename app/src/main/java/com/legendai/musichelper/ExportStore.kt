package com.legendai.musichelper

import android.content.Context

/**
 * Simple helper for tracking exported files using SharedPreferences.
 */
object ExportStore {
    private const val PREFS = "exports"
    private const val KEY_ENTRIES = "entries"

    data class Entry(val fileName: String, val time: Long)

    fun add(context: Context, fileName: String) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val set = prefs.getStringSet(KEY_ENTRIES, mutableSetOf())!!.toMutableSet()
        val entry = "$fileName|${System.currentTimeMillis()}"
        set.add(entry)
        prefs.edit().putStringSet(KEY_ENTRIES, set).apply()
    }

    fun list(context: Context): List<Entry> {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val set = prefs.getStringSet(KEY_ENTRIES, emptySet()) ?: emptySet()
        return set.mapNotNull {
            val parts = it.split('|', limit = 2)
            if (parts.size == 2) Entry(parts[0], parts[1].toLong()) else null
        }.sortedByDescending { it.time }
    }

    fun remove(context: Context, fileName: String) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val set = prefs.getStringSet(KEY_ENTRIES, mutableSetOf())!!.toMutableSet()
        set.removeIf { it.startsWith("$fileName|") }
        prefs.edit().putStringSet(KEY_ENTRIES, set).apply()
    }
}
