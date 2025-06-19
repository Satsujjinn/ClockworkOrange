package com.clockworkred.data.repository

import com.clockworkred.domain.ProjectRepository
import com.clockworkred.domain.model.ProjectModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

/** Simple in-memory repository returning fake data. */
@Singleton
class FakeProjectRepository @Inject constructor() : ProjectRepository {
    override fun getAllProjects(): Flow<List<ProjectModel>> = flow {
        delay(500)
        val projects = listOf(
            ProjectEntity("1", "Alpha", System.currentTimeMillis()),
            ProjectEntity("2", "Beta", System.currentTimeMillis()),
            ProjectEntity("3", "Gamma", System.currentTimeMillis())
        ).map { ProjectModel(it.id, it.name, it.createdAt) }
        emit(projects)
    }
}
