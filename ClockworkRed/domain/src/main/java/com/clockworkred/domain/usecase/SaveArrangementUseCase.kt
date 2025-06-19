package com.clockworkred.domain.usecase

import com.clockworkred.domain.ArrangementRepository
import com.clockworkred.domain.model.ArrangementStructure
import javax.inject.Inject

/** Persists an [ArrangementStructure]. */
class SaveArrangementUseCase @Inject constructor(
    private val repository: ArrangementRepository
) {
    suspend operator fun invoke(structure: ArrangementStructure) {
        repository.saveArrangement(structure)
    }
}
