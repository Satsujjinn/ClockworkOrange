package com.clockworkred.domain.usecase

import com.clockworkred.domain.ProjectRepository
import com.clockworkred.domain.model.ProjectModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/** Returns a stream of all projects. */
class GetAllProjectsUseCase @Inject constructor(
    private val repository: ProjectRepository
) {
    operator fun invoke(): Flow<List<ProjectModel>> = repository.getAllProjects()
}
