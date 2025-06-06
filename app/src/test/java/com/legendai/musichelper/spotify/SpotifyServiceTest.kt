package com.legendai.musichelper.spotify

import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class SpotifyServiceTest {
    private lateinit var server: MockWebServer
    private lateinit var service: SpotifyService

    @Before
    fun setup() {
        server = MockWebServer()
        server.start()
        val client = OkHttpClient.Builder().build()
        service = SpotifyService(client, baseUrl = server.url("/").toString())
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun searchAndAnalyze_makesRequests() {
        val searchJson = """
            {"tracks":{"items":[{"id":"id1","name":"Song","artists":[{"name":"Artist"}]}]}}
        """.trimIndent()
        val featuresJson = """
            {"tempo":120.0,"key":0,"mode":1}
        """.trimIndent()
        server.enqueue(MockResponse().setBody(searchJson))
        server.enqueue(MockResponse().setBody(featuresJson))

        val track = service.searchAndAnalyze("token", "song")

        val req1 = server.takeRequest()
        assertEquals("/search?q=song&type=track&limit=1", req1.path)
        val req2 = server.takeRequest()
        assertEquals("/audio-features/id1", req2.path)
        assertNotNull(track)
        assertEquals(120f, track!!.tempo)
    }
}
