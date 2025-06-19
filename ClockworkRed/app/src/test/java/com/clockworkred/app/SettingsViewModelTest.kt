package com.clockworkred.app

import com.clockworkred.domain.SettingsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class FakeSettingsRepository : SettingsRepository {
    private val _key = MutableStateFlow("")
    override val apiKey = _key
    override suspend fun saveApiKey(key: String) {
        _key.value = key
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {
    private lateinit var viewModel: SettingsViewModel
    private lateinit var repo: FakeSettingsRepository

    @Before
    fun setup() {
        repo = FakeSettingsRepository()
        viewModel = SettingsViewModel(repo)
    }

    @Test
    fun saveApiKey_updatesFlow() = runTest {
        viewModel.saveApiKey("123")
        assertEquals("123", repo.apiKey.value)
    }
}
