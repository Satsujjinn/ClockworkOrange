package com.clockworkred.app.projects

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clockworkred.domain.model.ProjectModel
import com.clockworkred.domain.usecase.GetAllProjectsUseCase
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
    private val getAllProjects: GetAllProjectsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProjectsUiState(isLoading = true))
    val uiState: StateFlow<ProjectsUiState> = _uiState

    init {
        viewModelScope.launch {
            getAllProjects()
                .onStart { _uiState.value = _uiState.value.copy(isLoading = true) }
                .catch { _uiState.value = ProjectsUiState(error = it.message) }
                .collect { projects ->
                    _uiState.value = ProjectsUiState(projects = projects, isLoading = false)
                }
        }
    }
}

/** UI state for [ProjectsScreen]. */
data class ProjectsUiState(
    val isLoading: Boolean = false,
    val projects: List<ProjectModel> = emptyList(),
    val error: String? = null
)
