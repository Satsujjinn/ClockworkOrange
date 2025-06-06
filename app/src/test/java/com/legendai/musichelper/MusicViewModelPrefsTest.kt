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
import kotlinx.coroutines.test.advanceUntilIdle
import okhttp3.OkHttpClient
import kotlin.test.assertEquals
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MusicViewModelPrefsTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var context: Context
    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var prefsRepo: UserPreferencesRepository
    private lateinit var viewModel: MusicViewModel

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        dataStore = PreferenceDataStoreFactory.create(
            scope = CoroutineScope(mainDispatcherRule.dispatcher + Job())
        ) { context.preferencesDataStoreFile("prefs_vm") }
        prefsRepo = UserPreferencesRepository(dataStore)
        viewModel = MusicViewModel(MusicRepository(OkHttpClient()), prefsRepo)
    }

    @After
    fun tearDown() {
        context.deleteFile("prefs_vm")
    }

    @Test
    fun preferencesLoadedAndUpdated() = runTest(mainDispatcherRule.dispatcher) {
        prefsRepo.setGenre("edm")
        prefsRepo.setKey("G")
        prefsRepo.setTempo(100f)
        prefsRepo.setDuration(40)

        // re-create viewModel to read stored values
        viewModel = MusicViewModel(MusicRepository(OkHttpClient()), prefsRepo)
        advanceUntilIdle()
        assertEquals("edm", viewModel.genre.value)
        assertEquals("G", viewModel.key.value)
        assertEquals(100f, viewModel.tempo.value)
        assertEquals(40, viewModel.duration.value)

        viewModel.setGenre("jazz")
        viewModel.setKey("D")
        viewModel.setTempo(80f)
        viewModel.setDuration(20)

        advanceUntilIdle()
        val prefs = prefsRepo.getPreferences()
        assertEquals("jazz", prefs.genre)
        assertEquals("D", prefs.key)
        assertEquals(80f, prefs.tempo)
        assertEquals(20, prefs.duration)
    }
}
