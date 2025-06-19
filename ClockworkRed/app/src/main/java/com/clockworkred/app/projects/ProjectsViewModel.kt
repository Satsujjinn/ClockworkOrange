package com.clockworkred.app.projects

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clockworkred.domain.model.ProjectModel
import com.clockworkred.domain.usecase.GetAllProjectsUseCase
import com.clockworkred.domain.usecase.CreateProjectUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

/** ViewModel for the projects screen. */
@HiltViewModel
class ProjectsViewModel @Inject constructor(
    private val getAllProjects: GetAllProjectsUseCase,
    private val createProjectUseCase: CreateProjectUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProjectsUiState(isLoading = true))
    val uiState: StateFlow<ProjectsUiState> = _uiState

    init {
        viewModelScope.launch {
            getAllProjects()
                .onStart { _uiState.value = _uiState.value.copy(isLoading = true) }
                .catch { _uiState.value = ProjectsUiState(error = it.message) }
                .collect { projects ->
                    _uiState.value = _uiState.value.copy(projects = projects, isLoading = false)
                }
        }
    }

    /** Creates a new project with the provided [name]. */
    fun createProject(name: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isCreating = true, creationError = null)
            val result = createProjectUseCase(name)
            result.fold(
                onSuccess = { project ->
                    _uiState.value = _uiState.value.copy(
                        projects = _uiState.value.projects + project,
                        isCreating = false,
                        creationError = null
                    )
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(isCreating = false, creationError = e.message)
                }
            )
        }
    }
}

/** UI state for [ProjectsScreen]. */
data class ProjectsUiState(
    val isLoading: Boolean = false,
    val projects: List<ProjectModel> = emptyList(),
    val error: String? = null,
    val isCreating: Boolean = false,
    val creationError: String? = null
)
