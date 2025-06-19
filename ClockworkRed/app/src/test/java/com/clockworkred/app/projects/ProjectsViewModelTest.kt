package com.clockworkred.app.projects

import com.clockworkred.data.repository.FakeProjectRepository
import com.clockworkred.domain.usecase.GetAllProjectsUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProjectsViewModelTest {
    private lateinit var viewModel: ProjectsViewModel

    @Before
    fun setup() {
        val repo = FakeProjectRepository()
        val useCase = GetAllProjectsUseCase(repo)
        viewModel = ProjectsViewModel(useCase)
    }

    @Test
    fun emitsLoadingThenData() = runTest {
        assertTrue(viewModel.uiState.value.isLoading)
        advanceTimeBy(600)
        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals(3, viewModel.uiState.value.projects.size)
    }
}
