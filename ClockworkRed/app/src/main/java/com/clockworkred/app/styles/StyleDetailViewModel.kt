package com.clockworkred.app.styles

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clockworkred.domain.model.StyleProfile
import com.clockworkred.domain.usecase.GetStyleByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

/** ViewModel for displaying a single style profile. */
@HiltViewModel
class StyleDetailViewModel @Inject constructor(
    private val getStyleById: GetStyleByIdUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(StyleDetailUiState())
    val uiState: StateFlow<StyleDetailUiState> = _uiState

    /** Load the style with the given [id]. */
    fun loadStyle(id: String) {
        viewModelScope.launch {
            getStyleById(id)
                .onStart { _uiState.value = _uiState.value.copy(isLoading = true, error = null) }
                .catch { _uiState.value = StyleDetailUiState(error = it.message) }
                .collect { style ->
                    _uiState.value = StyleDetailUiState(isLoading = false, style = style)
                }
        }
    }
}

/** UI state for [StyleDetailScreen]. */
data class StyleDetailUiState(
    val isLoading: Boolean = false,
    val style: StyleProfile? = null,
    val error: String? = null
)
