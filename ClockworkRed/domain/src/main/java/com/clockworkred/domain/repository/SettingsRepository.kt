package com.clockworkred.domain.repository

import kotlinx.coroutines.flow.Flow

/** Repository for persisting user settings. */
interface SettingsRepository {
    /** Emits the stored API key or null if not set. */
    fun getApiKey(): Flow<String?>

    /** Saves the provided API key. */
    suspend fun saveApiKey(key: String)
}
