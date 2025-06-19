package com.clockworkred.app.styles

import com.clockworkred.domain.StyleRepository
import com.clockworkred.domain.model.StyleProfile
import com.clockworkred.domain.usecase.GetAllStylesUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

private class FakeStyleRepository : StyleRepository {
    override fun getAllStyles(): Flow<List<StyleProfile>> = flow {
        emit(listOf(StyleProfile("id", "Name", "desc", emptyList(), emptyList(), emptyList())))
    }
    override fun getStyle(id: String): Flow<StyleProfile> = flow {
        emit(StyleProfile(id, "Name", "desc", emptyList(), emptyList(), emptyList()))
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class StyleListViewModelTest {
    private lateinit var viewModel: StyleListViewModel

    @Before
    fun setup() {
        val repo = FakeStyleRepository()
        viewModel = StyleListViewModel(GetAllStylesUseCase(repo))
    }

    @Test
    fun emitsLoadingThenData() = runTest {
        assertTrue(viewModel.uiState.value.isLoading)
        advanceUntilIdle()
        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals(1, viewModel.uiState.value.styles.size)
    }
}
