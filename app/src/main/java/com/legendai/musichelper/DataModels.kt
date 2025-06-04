package com.legendai.musichelper

import kotlinx.serialization.Serializable

// JSON request body for generateSong
@Serializable
data class GenerateSongRequest(
    val genre: String,
    val reference_audio_url: String = "",
    val duration_seconds: Int = 180,
    val instruments: List<String> = listOf("synth","bass","drums","solo"),
    val tempo: Int = 120,
    val key: String
)

// JSON response from generateSong
@Serializable
data class GenerateSongResponse(
    val synth_url: String,
    val bass_url: String,
    val drums_url: String,
    val solo_midi_url: String,
    val solo_wav_url: String,
    val chord_progressions: List<String>
)
