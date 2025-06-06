package com.legendai.musichelper

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.preferencesDataStoreFile
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class UserPreferencesRepositoryTest {
    private lateinit var context: Context
    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var repo: UserPreferencesRepository

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        dataStore = PreferenceDataStoreFactory.create(
            scope = CoroutineScope(Job())
        ) { context.preferencesDataStoreFile("prefs_test") }
        repo = UserPreferencesRepository(dataStore)
    }

    @After
    fun tearDown() {
        context.preferencesDataStoreFile("prefs_test").delete()
    }

    @Test
    fun valuesPersistAndRestore() = runTest {
        repo.setGenre("jazz")
        repo.setKey("D")
        repo.setTempo(90f)
        repo.setDuration(45)

        val restored = repo.getPreferences()
        assertEquals("jazz", restored.genre)
        assertEquals("D", restored.key)
        assertEquals(90f, restored.tempo)
        assertEquals(45, restored.duration)
    }
}
