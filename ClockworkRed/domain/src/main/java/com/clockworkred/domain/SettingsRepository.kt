package com.clockworkred.domain

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val apiKey: Flow<String>
    suspend fun saveApiKey(key: String)
}
