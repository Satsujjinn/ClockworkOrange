package com.clockworkred.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.clockworkred.domain.SettingsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore("settings")

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : SettingsRepository {
    private val KEY_API = stringPreferencesKey("api_key")

    override val apiKey: Flow<String> = context.dataStore.data.map { it[KEY_API] ?: "" }

    override suspend fun saveApiKey(key: String) {
        context.dataStore.edit { it[KEY_API] = key }
    }
}
