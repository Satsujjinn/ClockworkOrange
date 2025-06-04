package com.legendai.musichelper

import android.content.Context
import java.io.File
import com.legendai.musichelper.util.AudioMixer
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

    private val _stems = MutableStateFlow<GenerateSongResponse?>(null)
    val stems: StateFlow<GenerateSongResponse?> = _stems

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun generateSong(context: Context, request: GenerateSongRequest) {
        viewModelScope.launch {
            val apiKey = Config.getApiKey(context)
            try {
                _progress.value = 0.1f
                val response = repository.generateSong(apiKey, request)
                _stems.value = response
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
                // Download stems and merge them into single WAV file
                // This is a simplified placeholder mixdown using PCM concatenation
                val files = listOf(response.synth_url, response.bass_url, response.drums_url, response.solo_wav_url)
                val output = File(context.getExternalFilesDir(null), "mixdown.wav")
                AudioMixer.mixWavFiles(files, output)
                _error.value = "Saved to ${output.absolutePath}"
            } catch (e: Exception) {
                _error.value = "Network error—please retry"
            }
        }
    }
}
