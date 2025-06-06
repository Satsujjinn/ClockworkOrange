package com.legendai.musichelper

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.legendai.musichelper.BuildConfig

// Configuration values such as API base URL and API key loader
object Config {
    const val API_BASE_URL = "https://api-inference.huggingface.co/"

    fun getApiKey(context: Context): String {
        if (BuildConfig.MUSICGEN_API_KEY.isNotBlank()) {
            return BuildConfig.MUSICGEN_API_KEY
        }
        System.getenv("MUSICGEN_API_KEY")?.takeIf { it.isNotBlank() }?.let {
            return it
        }

        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        val prefs = EncryptedSharedPreferences.create(
            context,
            "legend_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
        return prefs.getString("API_KEY", "") ?: ""
    }

    fun setApiKey(context: Context, apiKey: String) {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        val prefs = EncryptedSharedPreferences.create(
            context,
            "legend_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
        prefs.edit().putString("API_KEY", apiKey).apply()
    }
}
