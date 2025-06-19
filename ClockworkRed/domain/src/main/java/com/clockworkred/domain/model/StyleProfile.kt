package com.clockworkred.domain.model

/** Describes a musical style for reference within the app. */
data class StyleProfile(
    val id: String,
    val name: String,
    val description: String,
    val characteristics: List<String>,
    val instrumentation: List<String>,
    val exampleSongs: List<String>
)
