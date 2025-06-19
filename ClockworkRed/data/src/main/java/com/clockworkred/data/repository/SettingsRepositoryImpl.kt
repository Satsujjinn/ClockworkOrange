package com.clockworkred.data.repository

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.clockworkred.data.local.dataStore
import com.clockworkred.domain.repository.SettingsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val API_KEY = stringPreferencesKey("api_key")

/** Implementation of [SettingsRepository] backed by [Preferences] DataStore. */
@Singleton
class SettingsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : SettingsRepository {

    override fun getApiKey(): Flow<String?> =
        context.dataStore.data.map { prefs -> prefs[API_KEY] }

    override suspend fun saveApiKey(key: String) {
        context.dataStore.edit { it[API_KEY] = key }
    }

    override suspend fun fetchRemoteFlags(): Map<String, String> {
        // TODO integrate Firebase Remote Config
        return emptyMap()
    }
}
