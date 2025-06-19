package com.clockworkred.data.repository

import com.clockworkred.data.remote.AiService
import com.clockworkred.data.remote.model.PartRequestDto
import com.clockworkred.domain.AiRepository
import com.clockworkred.domain.model.AiTabResult
import com.clockworkred.domain.model.PartRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

/** Implementation of [AiRepository] using [AiService]. */
@Singleton
class AiRepositoryImpl @Inject constructor(
    private val service: AiService
) : AiRepository {
    override fun generateTab(request: PartRequest): Flow<AiTabResult> = flow {
        val dto = PartRequestDto(
            instrument = request.instrument,
            style = request.style,
            references = request.references,
            section = request.section
        )
        val result = service.generateTab(dto)
        emit(AiTabResult(result.tabText, result.theoryNotes))
    }
}
