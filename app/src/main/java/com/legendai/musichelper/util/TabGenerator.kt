package com.legendai.musichelper.util

import com.legendai.musichelper.spotify.SpotifyTrack

object TabGenerator {
    private val names = listOf("C","C#","D","D#","E","F","F#","G","G#","A","A#","B")

    fun generate(track: SpotifyTrack, instrument: MelodyGenerator.Instrument): List<String> {
        val k = track.key ?: return listOf("Unknown key")
        val keyStr = names[k % names.size] + if ((track.mode ?: 1) == 0) "m" else ""
        val chords = ChordGenerator.suggest(keyStr, "rock")
        val melody = MelodyGenerator.generate(keyStr, instrument = instrument)
        return listOf("Chords: " + chords.joinToString(" - ")) + melody
    }
}
