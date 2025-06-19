package com.clockworkred.domain.usecase

import com.clockworkred.domain.ProjectRepository
import com.clockworkred.domain.model.ProjectModel
import javax.inject.Inject

/** Use case for creating a new project. */
class CreateProjectUseCase @Inject constructor(
    private val repository: ProjectRepository
) {
    suspend operator fun invoke(name: String): Result<ProjectModel> = runCatching {
        repository.createProject(name)
    }
}
