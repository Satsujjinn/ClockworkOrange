package com.clockworkred.app.arrangement

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clockworkred.domain.model.ArrangementStructure
import com.clockworkred.domain.usecase.GetArrangementStructureUseCase
import com.clockworkred.domain.usecase.SaveArrangementUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

/** ViewModel for the arrangement canvas screen. */
@HiltViewModel
class ArrangementViewModel @Inject constructor(
    private val getArrangement: GetArrangementStructureUseCase,
    private val saveArrangementUseCase: SaveArrangementUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ArrangementUiState())
    val uiState: StateFlow<ArrangementUiState> = _uiState

    init {
        viewModelScope.launch {
            getArrangement()
                .onStart { _uiState.value = _uiState.value.copy(isSaving = false, error = null) }
                .catch { _uiState.value = ArrangementUiState(error = it.message) }
                .collect { structure ->
                    _uiState.value = ArrangementUiState(structure = structure)
                }
        }
    }

    fun moveSection(from: Int, to: Int) {
        val sections = _uiState.value.structure.sections.toMutableList()
        if (from in sections.indices && to in sections.indices) {
            val item = sections.removeAt(from)
            sections.add(to, item)
            _uiState.value = _uiState.value.copy(
                structure = _uiState.value.structure.copy(
                    sections = sections.mapIndexed { index, s -> s.copy(position = index) }
                )
            )
        }
    }

    fun saveArrangement() {
        val structure = _uiState.value.structure
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isSaving = true)
                saveArrangementUseCase(structure)
                _uiState.value = _uiState.value.copy(isSaving = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isSaving = false, error = e.message)
            }
        }
    }
}

/** UI state for [ArrangementCanvasScreen]. */
data class ArrangementUiState(
    val structure: ArrangementStructure = ArrangementStructure(emptyList()),
    val isSaving: Boolean = false,
    val error: String? = null
)
