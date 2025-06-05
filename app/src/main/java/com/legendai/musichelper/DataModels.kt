package com.legendai.musichelper

import kotlinx.serialization.Serializable

// Request body for HuggingFace MusicGen
@Serializable
data class GenerateSongRequest(
    val inputs: String,
    val parameters: Parameters = Parameters()
)

@Serializable
data class Parameters(
    val duration: Int = 30
)

// Response containing saved audio path
data class GenerateSongResponse(
    val audioPath: String
)
