package com.clockworkred.app.settings

import com.clockworkred.domain.repository.SettingsRepository
import com.clockworkred.domain.usecase.GetApiKeyUseCase
import com.clockworkred.domain.usecase.SaveApiKeyUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test

private class FakeSettingsRepository : SettingsRepository {
    val state = MutableStateFlow<String?>(null)
    override fun getApiKey(): Flow<String?> = state.asStateFlow()
    override suspend fun saveApiKey(key: String) { state.value = key }
}

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {
    private lateinit var viewModel: SettingsViewModel
    private lateinit var repo: FakeSettingsRepository

    @Before
    fun setup() {
        repo = FakeSettingsRepository()
        val getUseCase = GetApiKeyUseCase(repo)
        val saveUseCase = SaveApiKeyUseCase(repo)
        viewModel = SettingsViewModel(getUseCase, saveUseCase)
    }

    @Test
    fun initialState_loadsApiKey() = runTest {
        repo.state.value = "abc"
        // recreate viewModel to observe initial value
        viewModel = SettingsViewModel(GetApiKeyUseCase(repo), SaveApiKeyUseCase(repo))
        advanceUntilIdle()
        assertEquals("abc", viewModel.uiState.value.apiKey)
    }

    @Test
    fun onSaveKey_updatesRepositoryAndTogglesFlag() = runTest {
        viewModel.onSaveKey("new")
        assertEquals(true, viewModel.uiState.value.isSaving)
        advanceUntilIdle()
        assertFalse(viewModel.uiState.value.isSaving)
        assertEquals("new", repo.state.value)
    }
}
