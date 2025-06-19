package com.clockworkred.data.repository

import com.clockworkred.domain.model.SongSection

/** Data entity mirroring [ArrangementStructure]. */
data class ArrangementEntity(
    val sections: List<SongSectionOrderEntity>
)

data class SongSectionOrderEntity(
    val section: SongSection,
    val position: Int
)
