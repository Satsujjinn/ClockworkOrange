package com.legendai.musichelper

import android.content.Context
import java.io.File
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// ViewModel handling business logic and exposing Compose states
@HiltViewModel
class MusicViewModel @Inject constructor(
    private val repository: MusicRepository
) : ViewModel() {

    private val _progress = MutableStateFlow(0f)
    val progress: StateFlow<Float> = _progress

    private val _audio = MutableStateFlow<GenerateSongResponse?>(null)
    val audio: StateFlow<GenerateSongResponse?> = _audio

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun generateSong(context: Context, request: GenerateSongRequest) {
        viewModelScope.launch {
            val apiKey = Config.getApiKey(context)
            try {
                _progress.value = 0.1f
                val response = repository.generateSong(apiKey, request, context)
                _audio.value = response
                _progress.value = 1f
            } catch (e: Exception) {
                _error.value = "Network error—please retry"
                _progress.value = 0f
            }
        }
    }

    fun clearError() { _error.value = null }
    fun mixdownAndExport(context: Context, response: GenerateSongResponse) {
        viewModelScope.launch {
            try {
                val input = File(response.audioPath)
                val output = File(context.getExternalFilesDir(null), "musicgen.wav")
                input.copyTo(output, overwrite = true)
                _error.value = "Saved to ${output.absolutePath}"
            } catch (e: Exception) {
                _error.value = "Network error—please retry"
            }
        }
    }
}
