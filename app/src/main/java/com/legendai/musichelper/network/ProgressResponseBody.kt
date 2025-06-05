package com.legendai.musichelper.network

import okhttp3.MediaType
import okhttp3.ResponseBody
import okio.*

/**
 * ResponseBody wrapper that reports download progress.
 */
class ProgressResponseBody(
    private val responseBody: ResponseBody,
    private val onProgress: (Float) -> Unit
) : ResponseBody() {

    private var bufferedSource: BufferedSource? = null
    private var totalBytesRead = 0L

    override fun contentLength(): Long = responseBody.contentLength()

    override fun contentType(): MediaType? = responseBody.contentType()

    override fun source(): BufferedSource {
        if (bufferedSource == null) {
            bufferedSource = source(responseBody.source()).buffer()
        }
        return bufferedSource!!
    }

    private fun source(source: Source): Source {
        val total = contentLength()
        return object : ForwardingSource(source) {
            override fun read(sink: Buffer, byteCount: Long): Long {
                val bytesRead = super.read(sink, byteCount)
                if (bytesRead != -1L) {
                    totalBytesRead += bytesRead
                    if (total > 0) {
                        onProgress(totalBytesRead.toFloat() / total)
                    }
                } else {
                    onProgress(1f)
                }
                return bytesRead
            }
        }
    }
}
