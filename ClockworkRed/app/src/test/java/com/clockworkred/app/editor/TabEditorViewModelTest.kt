package com.clockworkred.app.editor

import com.clockworkred.domain.model.AiTabResult
import com.clockworkred.domain.model.Instrument
import com.clockworkred.domain.model.PartRequest
import com.clockworkred.domain.model.SongSection
import com.clockworkred.domain.AiRepository
import com.clockworkred.domain.usecase.GenerateTabUseCase
import com.clockworkred.domain.usecase.GetAllStylesUseCase
import com.clockworkred.domain.StyleRepository
import com.clockworkred.domain.model.StyleProfile
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
    var lastRequest: PartRequest? = null
    override fun generateTab(request: PartRequest): Flow<AiTabResult> = flow {
        lastRequest = request
        emit(result)
    }
}

private class FakeStyleRepository : StyleRepository {
    val style = StyleProfile("id", "Name", "desc", emptyList(), emptyList(), emptyList())
    override fun getAllStyles(): Flow<List<StyleProfile>> = flow { emit(listOf(style)) }
    override fun getStyle(id: String): Flow<StyleProfile> = flow { emit(style) }
}

@OptIn(ExperimentalCoroutinesApi::class)
class TabEditorViewModelTest {
    private lateinit var viewModel: TabEditorViewModel
    private lateinit var useCase: GenerateTabUseCase
    private lateinit var fakeAi: FakeAiRepository
    private lateinit var styleUseCase: GetAllStylesUseCase

    @Before
    fun setup() {
        fakeAi = FakeAiRepository()
        useCase = GenerateTabUseCase(fakeAi)
        styleUseCase = GetAllStylesUseCase(FakeStyleRepository())
        viewModel = TabEditorViewModel(useCase, styleUseCase)
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

    @Test
    fun styleSelection_includesIdInRequest() = runTest {
        advanceUntilIdle()
        assertEquals(1, viewModel.styleOptions.value.size)
        viewModel.onStyleSelected("id")
        assertEquals("id", viewModel.selectedStyleId.value)
        viewModel.requestTab(PartRequest(Instrument.GUITAR, "rock", emptyList(), SongSection.CHORUS))
        advanceUntilIdle()
        assertEquals("id", fakeAi.lastRequest?.styleId)
    }
}
