package com.clockworkred.domain.usecase

import com.clockworkred.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/** Use case for retrieving the saved API key. */
class GetApiKeyUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    operator fun invoke(): Flow<String?> = repository.getApiKey()
}
