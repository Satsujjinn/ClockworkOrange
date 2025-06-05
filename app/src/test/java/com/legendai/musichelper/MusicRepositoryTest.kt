package com.legendai.musichelper

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okio.Buffer
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.File
import java.io.IOException
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFailsWith

class MusicRepositoryTest {
    private lateinit var server: MockWebServer
    private lateinit var repository: MusicRepository
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
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun generateSong_successReturnsFile() = runBlocking {
        val data = "OK".toByteArray()
        val buffer = Buffer().write(data)
        server.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(buffer)
                .setHeader("Content-Type", "application/octet-stream")
        )

        val result = repository.generateSong(
            apiKey = "token",
            request = GenerateSongRequest(inputs = "prompt"),
            context = context
        )

        val recorded = server.takeRequest()
        assertEquals("/models/facebook/musicgen-small", recorded.path)

        val file = File(result.audioPath)
        assertTrue(file.exists())
        assertEquals("OK", file.readText())
    }

    @Test
    fun generateSong_failureThrows() = runBlocking {
        server.enqueue(MockResponse().setResponseCode(500))

        assertFailsWith<IOException> {
            repository.generateSong(
                apiKey = "token",
                request = GenerateSongRequest(inputs = "prompt"),
                context = context
            )
        }
    }
}
