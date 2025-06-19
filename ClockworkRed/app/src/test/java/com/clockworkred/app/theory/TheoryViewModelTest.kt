package com.clockworkred.app.theory

import com.clockworkred.domain.model.TheoryNote
import com.clockworkred.domain.model.TheoryTopic
import com.clockworkred.domain.usecase.GetTheoryNoteUseCase
import com.clockworkred.domain.repository.TheoryRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.advanceUntilIdle
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

private class FakeTheoryRepository(
    private val result: Result<TheoryNote>
) : TheoryRepository {
    override suspend fun fetchNoteFor(topic: TheoryTopic): TheoryNote {
        return result.getOrElse { throw it }
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class TheoryViewModelTest {
    private lateinit var successUseCase: GetTheoryNoteUseCase
    private lateinit var errorUseCase: GetTheoryNoteUseCase

    @Before
    fun setup() {
        successUseCase = GetTheoryNoteUseCase(FakeTheoryRepository(Result.success(TheoryNote("title", "desc", emptyList()))))
        errorUseCase = GetTheoryNoteUseCase(FakeTheoryRepository(Result.failure(RuntimeException("fail"))))
    }

    @Test
    fun loadTopic_successUpdatesState() = runTest {
        val viewModel = TheoryViewModel(successUseCase)
        viewModel.loadTopic(TheoryTopic.SCALE)
        assertTrue(viewModel.uiState.value.isLoading)
        advanceUntilIdle()
        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals("title", viewModel.uiState.value.note?.title)
    }

    @Test
    fun loadTopic_errorSetsMessage() = runTest {
        val viewModel = TheoryViewModel(errorUseCase)
        viewModel.loadTopic(TheoryTopic.SCALE)
        advanceUntilIdle()
        assertEquals("fail", viewModel.uiState.value.error)
    }
}
