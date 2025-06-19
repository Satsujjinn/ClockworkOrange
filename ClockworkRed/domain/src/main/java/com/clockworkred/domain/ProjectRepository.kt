package com.clockworkred.domain

import com.clockworkred.domain.model.ProjectModel
import kotlinx.coroutines.flow.Flow

/** Repository for accessing projects. */
interface ProjectRepository {
    fun getAllProjects(): Flow<List<ProjectModel>>
    /** Creates a new project with the given [name]. */
    suspend fun createProject(name: String): ProjectModel
}
