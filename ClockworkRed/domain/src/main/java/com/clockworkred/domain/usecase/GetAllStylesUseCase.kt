package com.clockworkred.domain.usecase

import com.clockworkred.domain.StyleRepository
import com.clockworkred.domain.model.StyleProfile
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/** Use case to load all available style profiles. */
class GetAllStylesUseCase @Inject constructor(
    private val repository: StyleRepository
) {
    operator fun invoke(): Flow<List<StyleProfile>> = repository.getAllStyles()
}
