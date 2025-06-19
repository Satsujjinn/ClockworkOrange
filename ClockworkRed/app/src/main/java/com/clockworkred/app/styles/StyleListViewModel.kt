package com.clockworkred.app.styles

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clockworkred.domain.model.StyleProfile
import com.clockworkred.domain.usecase.GetAllStylesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

/** ViewModel for listing all style profiles. */
@HiltViewModel
class StyleListViewModel @Inject constructor(
    private val getAllStyles: GetAllStylesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(StyleListUiState(isLoading = true))
    val uiState: StateFlow<StyleListUiState> = _uiState

    init {
        viewModelScope.launch {
            getAllStyles()
                .onStart { _uiState.value = _uiState.value.copy(isLoading = true, error = null) }
                .catch { _uiState.value = StyleListUiState(error = it.message) }
                .collect { styles ->
                    _uiState.value = StyleListUiState(isLoading = false, styles = styles)
                }
        }
    }
}

/** UI state for [StyleListScreen]. */
data class StyleListUiState(
    val isLoading: Boolean = false,
    val styles: List<StyleProfile> = emptyList(),
    val error: String? = null
)
