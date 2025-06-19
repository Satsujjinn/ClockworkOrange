package com.clockworkred.domain.usecase

import com.clockworkred.domain.StyleRepository
import com.clockworkred.domain.model.StyleProfile
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/** Use case to retrieve a specific style profile by id. */
class GetStyleByIdUseCase @Inject constructor(
    private val repository: StyleRepository
) {
    operator fun invoke(id: String): Flow<StyleProfile> = repository.getStyle(id)
}
