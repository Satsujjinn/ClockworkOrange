package com.clockworkred.domain.model

/** Represents the ordered sections of a song arrangement. */
data class ArrangementStructure(
    val sections: List<SongSectionOrder>
)

/** Pairing of a [SongSection] with its position in the arrangement. */
data class SongSectionOrder(
    val section: SongSection,
    val position: Int
)
