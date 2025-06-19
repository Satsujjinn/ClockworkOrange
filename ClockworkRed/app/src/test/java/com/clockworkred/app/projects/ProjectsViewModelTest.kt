package com.clockworkred.app.projects

import com.clockworkred.domain.ProjectRepository
import com.clockworkred.domain.model.ProjectModel
import com.clockworkred.domain.usecase.CreateProjectUseCase
import com.clockworkred.domain.usecase.GetAllProjectsUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.delay
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProjectsViewModelTest {
    private lateinit var viewModel: ProjectsViewModel
    private lateinit var repo: TestProjectRepository

    @Before
    fun setup() {
        repo = TestProjectRepository()
        val getUseCase = GetAllProjectsUseCase(repo)
        val createUseCase = CreateProjectUseCase(repo)
        viewModel = ProjectsViewModel(getUseCase, createUseCase)
    }

    private class TestProjectRepository(var shouldFail: Boolean = false) : ProjectRepository {
        override fun getAllProjects(): Flow<List<ProjectModel>> = flow {
            delay(500)
            emit(
                listOf(
                    ProjectModel("1", "Alpha", 0L),
                    ProjectModel("2", "Beta", 0L),
                    ProjectModel("3", "Gamma", 0L)
                )
            )
        }

        override suspend fun createProject(name: String): ProjectModel {
            if (shouldFail) throw RuntimeException("fail")
            return ProjectModel("id", name, 0L)
        }
    }

    @Test
    fun emitsLoadingThenData() = runTest {
        assertTrue(viewModel.uiState.value.isLoading)
        advanceTimeBy(600)
        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals(3, viewModel.uiState.value.projects.size)
    }

    @Test
    fun createProject_successUpdatesState() = runTest {
        advanceUntilIdle()
        viewModel.createProject("New")
        assertTrue(viewModel.uiState.value.isCreating)
        advanceUntilIdle()
        assertFalse(viewModel.uiState.value.isCreating)
        assertEquals(1, viewModel.uiState.value.projects.size)
    }

    @Test
    fun createProject_failureSetsError() = runTest {
        repo.shouldFail = true
        advanceUntilIdle()
        viewModel.createProject("Bad")
        advanceUntilIdle()
        assertEquals("fail", viewModel.uiState.value.creationError)
    }
}
