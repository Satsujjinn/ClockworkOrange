package com.legendai.musichelper.util

import java.io.File
import java.io.InputStream
import java.io.RandomAccessFile
import java.net.URL
import java.nio.ByteBuffer
import java.nio.ByteOrder

// Simplified placeholder for mixing multiple WAV files into one
object AudioMixer {
    /**
     * Mix the PCM data of multiple WAV files and write the result to [output].
     *
     * The implementation assumes 16-bit little-endian PCM WAV files with
     * identical format. If files have different lengths the shorter ones are
     * padded with silence. The WAV header from the first file is preserved and
     * updated with the new data size.
     */
    fun mixWavFiles(urls: List<String>, output: File) {
        require(urls.isNotEmpty()) { "No input files provided" }

        data class Input(val header: ByteArray, val stream: InputStream, var remaining: Int)

        // Prepare input streams and read their headers
        val inputs = urls.map { urlStr ->
            val stream = URL(urlStr).openStream().buffered()
            val header = ByteArray(44)
            val read = stream.read(header)
            require(read == 44) { "Invalid WAV header for $urlStr" }
            val dataSize = ByteBuffer.wrap(header, 40, 4)
                .order(ByteOrder.LITTLE_ENDIAN).int
            Input(header, stream, dataSize)
        }

        val baseHeader = inputs.first().header.clone()

        val bufferSamples = 4096
        val inBuffers = Array(inputs.size) { ByteArray(bufferSamples * 2) }
        val shortBuffers = Array(inputs.size) { ShortArray(bufferSamples) }
        val shortCounts = IntArray(inputs.size)
        val outShorts = ShortArray(bufferSamples)
        val outBytes = ByteArray(bufferSamples * 2)

        var totalSamples = 0

        output.outputStream().use { out ->
            out.write(baseHeader) // placeholder

            while (inputs.any { it.remaining > 0 }) {
                var maxSamplesRead = 0
                for (i in inputs.indices) {
                    val input = inputs[i]
                    if (input.remaining > 0) {
                        val toRead = minOf(bufferSamples * 2, input.remaining)
                        val readBytes = input.stream.read(inBuffers[i], 0, toRead)
                        if (readBytes > 0) {
                            input.remaining -= readBytes
                            val bb = ByteBuffer.wrap(inBuffers[i], 0, readBytes)
                                .order(ByteOrder.LITTLE_ENDIAN)
                            val sc = readBytes / 2
                            shortCounts[i] = sc
                            for (s in 0 until sc) {
                                shortBuffers[i][s] = bb.short
                            }
                            if (sc < bufferSamples) {
                                java.util.Arrays.fill(shortBuffers[i], sc, bufferSamples, 0)
                            }
                            maxSamplesRead = maxOf(maxSamplesRead, sc)
                        } else {
                            shortCounts[i] = 0
                        }
                    } else {
                        shortCounts[i] = 0
                    }
                }

                if (maxSamplesRead == 0) {
                    break
                }

                for (s in 0 until maxSamplesRead) {
                    var sum = 0
                    var count = 0
                    for (i in inputs.indices) {
                        if (s < shortCounts[i]) {
                            sum += shortBuffers[i][s].toInt()
                            count++
                        }
                    }
                    outShorts[s] = if (count == 0) 0 else (sum / count).toShort()
                }

                val bbOut = ByteBuffer.wrap(outBytes).order(ByteOrder.LITTLE_ENDIAN)
                for (i in 0 until maxSamplesRead) {
                    bbOut.putShort(outShorts[i])
                }
                out.write(outBytes, 0, maxSamplesRead * 2)
                totalSamples += maxSamplesRead
            }
        }

        val dataSize = totalSamples * 2
        ByteBuffer.wrap(baseHeader, 40, 4).order(ByteOrder.LITTLE_ENDIAN).putInt(dataSize)
        ByteBuffer.wrap(baseHeader, 4, 4).order(ByteOrder.LITTLE_ENDIAN).putInt(36 + dataSize)

        RandomAccessFile(output, "rw").use { raf ->
            raf.seek(0)
            raf.write(baseHeader)
        }

        inputs.forEach { it.stream.close() }
    }
}
