package com.clockworkred.data.remote.model

import com.clockworkred.domain.model.Instrument
import com.clockworkred.domain.model.SongSection

/** DTO mirroring [PartRequest]. */
data class PartRequestDto(
    val instrument: Instrument,
    val style: String,
    val references: List<String>,
    val section: SongSection,
    val styleId: String?
)
