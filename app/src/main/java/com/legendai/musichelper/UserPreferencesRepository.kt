package com.legendai.musichelper

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first

class UserPreferencesRepository(
    private val dataStore: DataStore<Preferences>
) {
    private object Keys {
        val GENRE = stringPreferencesKey("genre")
        val KEY = stringPreferencesKey("key")
        val TEMPO = floatPreferencesKey("tempo")
        val DURATION = intPreferencesKey("duration")
    }

    suspend fun getPreferences(): UserPreferences {
        val prefs = dataStore.data.first()
        return UserPreferences(
            genre = prefs[Keys.GENRE] ?: "rock",
            key = prefs[Keys.KEY] ?: "C",
            tempo = prefs[Keys.TEMPO] ?: 120f,
            duration = prefs[Keys.DURATION] ?: 30
        )
    }

    suspend fun setGenre(value: String) {
        dataStore.edit { it[Keys.GENRE] = value }
    }

    suspend fun setKey(value: String) {
        dataStore.edit { it[Keys.KEY] = value }
    }

    suspend fun setTempo(value: Float) {
        dataStore.edit { it[Keys.TEMPO] = value }
    }

    suspend fun setDuration(value: Int) {
        dataStore.edit { it[Keys.DURATION] = value }
    }
}
