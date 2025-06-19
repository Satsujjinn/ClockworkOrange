package com.clockworkred.app.projects

import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/** Simple JUnit5 test ensuring initial loading state. */
class ProjectsViewModelJUnit5Test {
    @Test
    fun startsLoading() = runTest {
        val repo = object : com.clockworkred.domain.ProjectRepository {
            override fun getAllProjects() = kotlinx.coroutines.flow.flow<List<com.clockworkred.domain.model.ProjectModel>> { emit(emptyList()) }
            override suspend fun createProject(name: String) = com.clockworkred.domain.model.ProjectModel("id", name, 0L)
        }
        val viewModel = ProjectsViewModel(
            com.clockworkred.domain.usecase.GetAllProjectsUseCase(repo),
            com.clockworkred.domain.usecase.CreateProjectUseCase(repo)
        )
        assertTrue(viewModel.uiState.value.isLoading)
    }
}
