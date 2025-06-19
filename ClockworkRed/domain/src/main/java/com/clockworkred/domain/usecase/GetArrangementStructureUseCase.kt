package com.clockworkred.domain.usecase

import com.clockworkred.domain.ArrangementRepository
import com.clockworkred.domain.model.ArrangementStructure
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/** Use case to retrieve the current arrangement structure. */
class GetArrangementStructureUseCase @Inject constructor(
    private val repository: ArrangementRepository
) {
    operator fun invoke(): Flow<ArrangementStructure> = repository.getArrangement()
}
