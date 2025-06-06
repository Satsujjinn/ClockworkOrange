package com.legendai.musichelper

import com.legendai.musichelper.network.ProgressResponseBody
import okhttp3.MediaType
import okhttp3.ResponseBody
import okio.Buffer
import okio.BufferedSource
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ProgressResponseBodyTest {
    @Test
    fun progressReachesOneAndMonotonic() {
        val bytes = ByteArray(1024) { it.toByte() }
        val body = object : ResponseBody() {
            override fun contentType(): MediaType? = null
            override fun contentLength(): Long = bytes.size.toLong()
            override fun source(): BufferedSource = Buffer().write(bytes)
        }

        val progressValues = mutableListOf<Float>()
        val progressBody = ProgressResponseBody(body) { progressValues.add(it) }

        val sink = Buffer()
        val source = progressBody.source()
        while (source.read(sink, 8192) != -1L) {
            // reading until end
        }

        assertEquals(1f, progressValues.last())
        for (i in 1 until progressValues.size) {
            assertTrue(progressValues[i] >= progressValues[i - 1])
        }
    }
}
