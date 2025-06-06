package com.legendai.musichelper.web

import kotlinx.serialization.Serializable

@Serializable
data class GenerateSongRequest(
    val inputs: String,
    val parameters: Parameters = Parameters()
)

@Serializable
data class Parameters(
    val duration: Int = 30
)
