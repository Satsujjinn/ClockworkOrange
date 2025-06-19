package com.clockworkred.app.editor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clockworkred.domain.model.AiTabResult
import com.clockworkred.domain.model.PartRequest
import com.clockworkred.domain.usecase.GenerateTabUseCase
import com.clockworkred.domain.usecase.GetAllStylesUseCase
import com.clockworkred.domain.model.StyleProfile
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
    private val generateTab: GenerateTabUseCase,
    private val getAllStyles: GetAllStylesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditorUiState())
    val uiState: StateFlow<EditorUiState> = _uiState

    private val _styleOptions = MutableStateFlow<List<StyleProfile>>(emptyList())
    val styleOptions: StateFlow<List<StyleProfile>> = _styleOptions

    val selectedStyleId = MutableStateFlow<String?>(null)

    init {
        viewModelScope.launch {
            getAllStyles()
                .catch { /* ignore errors for now */ }
                .collect { _styleOptions.value = it }
        }
    }

    /** Request a new tab from the AI service. */
    fun requestTab(request: PartRequest) {
        viewModelScope.launch {
            val withStyle = request.copy(styleId = selectedStyleId.value)
            generateTab(withStyle)
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

    /** Update the currently selected style. */
    fun onStyleSelected(id: String) {
        selectedStyleId.value = id
    }
}

/** UI state for [TabEditorScreen]. */
data class EditorUiState(
    val isLoading: Boolean = false,
    val tabText: String = "",
    val theoryNotes: String? = null,
    val error: String? = null
)
