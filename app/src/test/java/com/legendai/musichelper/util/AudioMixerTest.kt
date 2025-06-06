package com.legendai.musichelper.util

import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.test.Test
import kotlin.test.assertEquals

class AudioMixerTest {
    private fun createWav(samples: ShortArray, sampleRate: Int = 44100): File {
        val numChannels = 1
        val bitsPerSample = 16
        val byteRate = sampleRate * numChannels * bitsPerSample / 8
        val blockAlign = (numChannels * bitsPerSample / 8).toShort()
        val subchunk2Size = samples.size * 2
        val chunkSize = 36 + subchunk2Size
        val header = ByteBuffer.allocate(44).order(ByteOrder.LITTLE_ENDIAN).apply {
            put("RIFF".toByteArray())
            putInt(chunkSize)
            put("WAVE".toByteArray())
            put("fmt ".toByteArray())
            putInt(16)
            putShort(1)
            putShort(numChannels.toShort())
            putInt(sampleRate)
            putInt(byteRate)
            putShort(blockAlign)
            putShort(bitsPerSample.toShort())
            put("data".toByteArray())
            putInt(subchunk2Size)
        }.array()
        val pcm = ByteBuffer.allocate(subchunk2Size).order(ByteOrder.LITTLE_ENDIAN)
        for (s in samples) pcm.putShort(s)
        val file = File.createTempFile("mix_test_", ".wav")
        file.outputStream().use { out ->
            out.write(header)
            out.write(pcm.array())
        }
        return file
    }

    @Test
    fun mixPreservesDuration() {
        val s1 = ShortArray(44100) { 1000 }
        val s2 = ShortArray(44100) { -1000 }
        val f1 = createWav(s1)
        val f2 = createWav(s2)
        val out = File.createTempFile("mix_out_", ".wav")
        AudioMixer.mixWavFiles(listOf(f1.toURI().toString(), f2.toURI().toString()), out)
        val header = ByteArray(44)
        out.inputStream().use { input ->
            input.read(header)
            val subSize = ByteBuffer.wrap(header, 40, 4)
                .order(ByteOrder.LITTLE_ENDIAN).int
            assertEquals(s1.size * 2, subSize)
            val firstBytes = ByteArray(2)
            input.read(firstBytes)
            val firstSample = ByteBuffer.wrap(firstBytes)
                .order(ByteOrder.LITTLE_ENDIAN).short
            assertEquals(0, firstSample)
        }
    }

    @Test
    fun mixDifferentLengthsPadsSilence() {
        val shortSamples = ShortArray(5) { 1000 }
        val longSamples = ShortArray(10) { (it * 100).toShort() }

        val shortFile = createWav(shortSamples)
        val longFile = createWav(longSamples)
        val out = File.createTempFile("mix_out_", ".wav")

        AudioMixer.mixWavFiles(
            listOf(shortFile.toURI().toString(), longFile.toURI().toString()),
            out
        )

        val header = ByteArray(44)
        val mixed: ShortArray
        out.inputStream().use { input ->
            input.read(header)
            val dataSize = ByteBuffer.wrap(header, 40, 4)
                .order(ByteOrder.LITTLE_ENDIAN).int
            assertEquals(longSamples.size * 2, dataSize)

            val sampleCount = dataSize / 2
            mixed = ShortArray(sampleCount)
            val buffer = ByteArray(256)
            var index = 0
            while (index < sampleCount) {
                val toRead = minOf(buffer.size, (sampleCount - index) * 2)
                val r = input.read(buffer, 0, toRead)
                if (r <= 0) break
                val bb = ByteBuffer.wrap(buffer, 0, r)
                    .order(ByteOrder.LITTLE_ENDIAN)
                val sc = r / 2
                for (i in 0 until sc) {
                    mixed[index++] = bb.short
                }
            }
        }

        // Beyond the end of the short file only the long file contributes
        val idx = shortSamples.size + 1
        assertEquals(longSamples[idx], mixed[idx])
        assertEquals(longSamples.last(), mixed.last())
    }
}
