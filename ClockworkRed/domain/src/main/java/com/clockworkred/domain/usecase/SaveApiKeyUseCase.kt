package com.clockworkred.domain.usecase

import com.clockworkred.domain.repository.SettingsRepository
import javax.inject.Inject

/** Use case for persisting the API key. */
class SaveApiKeyUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    suspend operator fun invoke(key: String) {
        repository.saveApiKey(key)
    }
}
