package com.clockworkred.app.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clockworkred.domain.usecase.GetApiKeyUseCase
import com.clockworkred.domain.usecase.SaveApiKeyUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

/** ViewModel for the settings screen handling API key management. */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val getApiKey: GetApiKeyUseCase,
    private val saveApiKey: SaveApiKeyUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getApiKey().collectLatest { key ->
                _uiState.value = _uiState.value.copy(apiKey = key)
            }
        }
    }

    /** Persist a new API key. */
    fun onSaveKey(newKey: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, error = null)
            try {
                saveApiKey(newKey)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
            _uiState.value = _uiState.value.copy(isSaving = false)
        }
    }
}

/** UI state for [SettingsScreen]. */
data class SettingsUiState(
    val apiKey: String? = null,
    val isSaving: Boolean = false,
    val error: String? = null
)
