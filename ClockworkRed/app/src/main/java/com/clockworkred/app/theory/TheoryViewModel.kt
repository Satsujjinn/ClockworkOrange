package com.clockworkred.app.theory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clockworkred.domain.model.TheoryNote
import com.clockworkred.domain.model.TheoryTopic
import com.clockworkred.domain.usecase.GetTheoryNoteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/** ViewModel for fetching theory notes. */
@HiltViewModel
class TheoryViewModel @Inject constructor(
    private val getTheoryNote: GetTheoryNoteUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(TheoryUiState(isLoading = false, note = null, error = null))
    val uiState: StateFlow<TheoryUiState> = _uiState

    /** Load the note for the provided [topic]. */
    fun loadTopic(topic: TheoryTopic) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = getTheoryNote(topic)
            result.fold(
                onSuccess = { note -> _uiState.value = TheoryUiState(false, note, null) },
                onFailure = { err -> _uiState.value = TheoryUiState(false, null, err.message) }
            )
        }
    }
}

/** UI state for [TheoryHelperScreen]. */
data class TheoryUiState(
    val isLoading: Boolean,
    val note: TheoryNote?,
    val error: String?
)
