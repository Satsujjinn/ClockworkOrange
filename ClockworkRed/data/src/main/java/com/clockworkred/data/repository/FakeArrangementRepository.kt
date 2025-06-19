package com.clockworkred.data.repository

import com.clockworkred.domain.ArrangementRepository
import com.clockworkred.domain.model.ArrangementStructure
import com.clockworkred.domain.model.SongSection
import com.clockworkred.domain.model.SongSectionOrder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/** Fake implementation of [ArrangementRepository] for testing. */
@Singleton
class FakeArrangementRepository @Inject constructor() : ArrangementRepository {
    private val defaultStructure = ArrangementStructure(
        sections = listOf(
            SongSectionOrder(SongSection.INTRO, 0),
            SongSectionOrder(SongSection.VERSE, 1),
            SongSectionOrder(SongSection.CHORUS, 2),
            SongSectionOrder(SongSection.BRIDGE, 3),
            SongSectionOrder(SongSection.OUTRO, 4)
        )
    )

    private val structureFlow = MutableStateFlow(defaultStructure)

    override fun getArrangement(): Flow<ArrangementStructure> = structureFlow.asStateFlow()

    override suspend fun saveArrangement(structure: ArrangementStructure) {
        structureFlow.value = structure
        // no-op persistence for fake
    }
}
