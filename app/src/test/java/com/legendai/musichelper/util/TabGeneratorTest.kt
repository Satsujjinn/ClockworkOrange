package com.legendai.musichelper.util

import com.legendai.musichelper.spotify.SpotifyTrack
import org.junit.Test
import kotlin.test.assertTrue

class TabGeneratorTest {
    @Test
    fun generateFromTrack() {
        val track = SpotifyTrack("1", "Song", "Artist", tempo = 120f, key = 0, mode = 1)
        val tab = TabGenerator.generate(track, MelodyGenerator.Instrument.BASS)
        assertTrue(tab.isNotEmpty())
    }
}
