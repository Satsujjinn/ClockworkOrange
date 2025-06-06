package com.legendai.musichelper

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.legendai.musichelper.R
import java.io.File
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okio.Buffer
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class MusicViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var server: MockWebServer
    private lateinit var repository: MusicRepository
    private lateinit var viewModel: MusicViewModel
    private lateinit var context: Context

    @Before
    fun setup() {
        server = MockWebServer()
        server.start()
        context = ApplicationProvider.getApplicationContext()
        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val original = chain.request()
                val newUrl = server.url("/").newBuilder()
                    .encodedPath(original.url.encodedPath)
                    .build()
                val request = original.newBuilder().url(newUrl).build()
                chain.proceed(request)
            }
            .build()
        repository = MusicRepository(client)
        viewModel = MusicViewModel(repository)
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun generateSong_success_updatesState() = runTest(mainDispatcherRule.dispatcher) {
        val data = "OK".toByteArray()
        server.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(Buffer().write(data))
                .setHeader("Content-Type", "application/octet-stream")
        )

        viewModel.generateSong(context, GenerateSongRequest("prompt"), "C", "rock")
        advanceUntilIdle()

        assertEquals(1f, viewModel.progress.value)
        assertNotNull(viewModel.audio.value)
        assertTrue(viewModel.chords.value.isNotEmpty())
        assertNull(viewModel.error.value)
    }

    @Test
    fun generateSong_failure_setsError() = runTest(mainDispatcherRule.dispatcher) {
        server.enqueue(MockResponse().setResponseCode(500))

        viewModel.generateSong(context, GenerateSongRequest("prompt"), "C", "rock")
        advanceUntilIdle()

        assertEquals(0f, viewModel.progress.value)
        assertNull(viewModel.audio.value)
        assertEquals(context.getString(R.string.error_network), viewModel.error.value)
    }

    @Test
    fun generateSong_noApiKey_setsError() = runTest(mainDispatcherRule.dispatcher) {
        Config.setApiKey(context, "")

        viewModel.generateSong(context, GenerateSongRequest("prompt"), "C", "rock")
        advanceUntilIdle()

        assertEquals(0f, viewModel.progress.value)
        assertNull(viewModel.audio.value)
        assertTrue(viewModel.chords.value.isEmpty())
        assertEquals(context.getString(R.string.error_no_api_key), viewModel.error.value)
    }

    @Test
    fun mixdownAndExport_nullDir_setsError() = runTest(mainDispatcherRule.dispatcher) {
        val audio = File.createTempFile("clip_", ".wav")
        val wrapper = object : android.content.ContextWrapper(context) {
            override fun getExternalFilesDir(type: String?): File? = null
        }

        viewModel.mixdownAndExport(wrapper, GenerateSongResponse(audio.absolutePath))
        advanceUntilIdle()

        assertEquals(context.getString(R.string.error_storage_unavailable), viewModel.error.value)
    }

    @Test
    fun mixAndExport_nullDir_setsError() = runTest(mainDispatcherRule.dispatcher) {
        val audio = File.createTempFile("clip_", ".wav")
        val wrapper = object : android.content.ContextWrapper(context) {
            override fun getExternalFilesDir(type: String?): File? = null
        }

        viewModel.mixAndExport(wrapper, listOf(GenerateSongResponse(audio.absolutePath)))
        advanceUntilIdle()

        assertEquals(context.getString(R.string.error_storage_unavailable), viewModel.error.value)
    }
}
