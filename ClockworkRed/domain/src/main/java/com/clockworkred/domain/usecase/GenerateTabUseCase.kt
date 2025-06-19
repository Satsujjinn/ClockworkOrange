package com.clockworkred.domain.usecase

import com.clockworkred.domain.AiRepository
import com.clockworkred.domain.model.AiTabResult
import com.clockworkred.domain.model.PartRequest
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/** Use case for requesting AI-generated tabs. */
class GenerateTabUseCase @Inject constructor(
    private val repository: AiRepository
) {
    operator fun invoke(request: PartRequest): Flow<AiTabResult> =
        repository.generateTab(request)
}
