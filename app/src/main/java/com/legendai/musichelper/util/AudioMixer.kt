package com.legendai.musichelper.util

import java.io.File
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

        // Read header and PCM data from each input
        val inputs = urls.map { urlStr ->
            val bytes = URL(urlStr).readBytes()
            val header = bytes.copyOfRange(0, 44)
            val pcm = bytes.copyOfRange(44, bytes.size)
            val shortCount = pcm.size / 2
            val samples = ShortArray(shortCount)
            val bb = ByteBuffer.wrap(pcm).order(ByteOrder.LITTLE_ENDIAN)
            for (i in 0 until shortCount) {
                samples[i] = bb.short
            }
            header to samples
        }

        val baseHeader = inputs.first().first.copyOf()
        val maxSamples = inputs.maxOf { it.second.size }
        val mixed = ShortArray(maxSamples)

        for (i in 0 until maxSamples) {
            var sum = 0
            var count = 0
            for ((_, data) in inputs) {
                if (i < data.size) {
                    sum += data[i].toInt()
                    count++
                }
            }
            mixed[i] = if (count == 0) 0 else (sum / count).toShort()
        }

        // Update header sizes
        val dataSize = mixed.size * 2
        ByteBuffer.wrap(baseHeader, 40, 4).order(ByteOrder.LITTLE_ENDIAN).putInt(dataSize)
        ByteBuffer.wrap(baseHeader, 4, 4).order(ByteOrder.LITTLE_ENDIAN).putInt(36 + dataSize)

        // Write header and mixed samples
        output.outputStream().use { out ->
            out.write(baseHeader)
            val buffer = ByteBuffer.allocate(dataSize).order(ByteOrder.LITTLE_ENDIAN)
            for (s in mixed) buffer.putShort(s)
            out.write(buffer.array())
        }
    }
}
