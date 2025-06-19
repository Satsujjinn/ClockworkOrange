package com.clockworkred.domain

import com.clockworkred.domain.model.ArrangementStructure
import kotlinx.coroutines.flow.Flow

/** Repository for accessing and saving arrangement data. */
interface ArrangementRepository {
    fun getArrangement(): Flow<ArrangementStructure>
    suspend fun saveArrangement(structure: ArrangementStructure)
}
