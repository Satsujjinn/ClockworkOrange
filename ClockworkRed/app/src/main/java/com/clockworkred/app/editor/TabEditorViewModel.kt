package com.clockworkred.app.editor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clockworkred.domain.model.AiTabResult
import com.clockworkred.domain.model.PartRequest
import com.clockworkred.domain.usecase.GenerateTabUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

/** ViewModel for the tab editor screen. */
@HiltViewModel
class TabEditorViewModel @Inject constructor(
    private val generateTab: GenerateTabUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditorUiState())
    val uiState: StateFlow<EditorUiState> = _uiState

    /** Request a new tab from the AI service. */
    fun requestTab(request: PartRequest) {
        viewModelScope.launch {
            generateTab(request)
                .onStart { _uiState.value = _uiState.value.copy(isLoading = true, error = null) }
                .catch { _uiState.value = EditorUiState(error = it.message) }
                .collect { result: AiTabResult ->
                    _uiState.value = EditorUiState(
                        isLoading = false,
                        tabText = result.tabText,
                        theoryNotes = result.theoryNotes
                    )
                }
        }
    }
}

/** UI state for [TabEditorScreen]. */
data class EditorUiState(
    val isLoading: Boolean = false,
    val tabText: String = "",
    val theoryNotes: String? = null,
    val error: String? = null
)
