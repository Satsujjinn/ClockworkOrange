package com.legendai.musichelper

data class UserPreferences(
    val genre: String = "rock",
    val key: String = "C",
    val tempo: Float = 120f,
    val duration: Int = 30
)
