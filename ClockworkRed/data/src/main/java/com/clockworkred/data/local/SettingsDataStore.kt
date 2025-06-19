package com.clockworkred.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

/** DataStore for persisting ClockworkRed settings. */
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "clockworkred_settings"
)
// TODO migrate to Proto DataStore for type safety
