package com.clockworkred.app.editor

import com.clockworkred.domain.model.AiTabResult
import com.clockworkred.domain.model.Instrument
import com.clockworkred.domain.model.PartRequest
import com.clockworkred.domain.model.SongSection
import com.clockworkred.domain.AiRepository
import com.clockworkred.domain.usecase.GenerateTabUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.advanceUntilIdle
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class FakeAiRepository : AiRepository {
    private val result = AiTabResult("tab", "notes")
    override fun generateTab(request: PartRequest): Flow<AiTabResult> = flow { emit(result) }
}

@OptIn(ExperimentalCoroutinesApi::class)
class TabEditorViewModelTest {
    private lateinit var viewModel: TabEditorViewModel
    private lateinit var useCase: GenerateTabUseCase

    @Before
    fun setup() {
        useCase = GenerateTabUseCase(FakeAiRepository())
        viewModel = TabEditorViewModel(useCase)
    }

    @Test
    fun requestTab_emitsLoadingAndResult() = runTest {
        viewModel.requestTab(PartRequest(Instrument.GUITAR, "rock", emptyList(), SongSection.CHORUS))
        assertTrue(viewModel.uiState.value.isLoading)
        advanceUntilIdle()
        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals("tab", viewModel.uiState.value.tabText)
        assertEquals("notes", viewModel.uiState.value.theoryNotes)
    }
}
