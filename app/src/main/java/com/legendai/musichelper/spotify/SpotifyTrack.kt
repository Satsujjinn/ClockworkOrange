package com.legendai.musichelper.spotify

data class SpotifyTrack(
    val id: String,
    val name: String,
    val artist: String,
    val tempo: Float? = null,
    val key: Int? = null,
    val mode: Int? = null
)
