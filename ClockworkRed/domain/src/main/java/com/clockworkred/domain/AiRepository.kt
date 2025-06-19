package com.clockworkred.domain

import com.clockworkred.domain.model.AiTabResult
import com.clockworkred.domain.model.PartRequest
import kotlinx.coroutines.flow.Flow

/** Repository for generating AI-powered tabs. */
interface AiRepository {
    fun generateTab(request: PartRequest): Flow<AiTabResult>
}
