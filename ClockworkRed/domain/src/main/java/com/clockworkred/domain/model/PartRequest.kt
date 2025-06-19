package com.clockworkred.domain.model

/** Request parameters for generating an instrument part. */
data class PartRequest(
    val instrument: Instrument,
    val style: String,
    val references: List<String>,
    val section: SongSection
)
