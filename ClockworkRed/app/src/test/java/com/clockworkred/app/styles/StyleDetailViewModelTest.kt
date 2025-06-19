package com.clockworkred.app.styles

import com.clockworkred.domain.StyleRepository
import com.clockworkred.domain.model.StyleProfile
import com.clockworkred.domain.usecase.GetStyleByIdUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

private class FakeStyleRepository : StyleRepository {
    var shouldFail = false
    private val style = StyleProfile("id", "Name", "desc", emptyList(), emptyList(), emptyList())
    override fun getAllStyles(): Flow<List<StyleProfile>> = flow { emit(listOf(style)) }
    override fun getStyle(id: String): Flow<StyleProfile> = flow {
        if (shouldFail || id != style.id) throw NoSuchElementException("not found")
        emit(style)
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class StyleDetailViewModelTest {
    private lateinit var viewModel: StyleDetailViewModel
    private lateinit var repo: FakeStyleRepository

    @Before
    fun setup() {
        repo = FakeStyleRepository()
        viewModel = StyleDetailViewModel(GetStyleByIdUseCase(repo))
    }

    @Test
    fun loadStyle_successEmitsData() = runTest {
        viewModel.loadStyle("id")
        advanceUntilIdle()
        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals("id", viewModel.uiState.value.style?.id)
    }

    @Test
    fun loadStyle_missingIdSetsError() = runTest {
        viewModel.loadStyle("bad")
        advanceUntilIdle()
        assertNull(viewModel.uiState.value.style)
        assertTrue(viewModel.uiState.value.error != null)
    }
}
