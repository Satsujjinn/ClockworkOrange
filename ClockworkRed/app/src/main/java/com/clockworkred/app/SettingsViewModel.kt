package com.clockworkred.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clockworkred.domain.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: SettingsRepository
) : ViewModel() {
    val apiKey = repository.apiKey.stateIn(viewModelScope, SharingStarted.Lazily, "")

    fun saveApiKey(key: String) {
        viewModelScope.launch { repository.saveApiKey(key) }
    }
}
