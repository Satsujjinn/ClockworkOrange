package com.legendai.musichelper

import android.content.Context
import java.io.File
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.legendai.musichelper.util.ChordGenerator
import com.legendai.musichelper.util.AudioMixer

// ViewModel handling business logic and exposing Compose states
class MusicViewModel(
    private val repository: MusicRepository
) : ViewModel() {

    private val _progress = MutableStateFlow(0f)
    val progress: StateFlow<Float> = _progress

    private val _audio = MutableStateFlow<GenerateSongResponse?>(null)
    val audio: StateFlow<GenerateSongResponse?> = _audio

    private val _clips = MutableStateFlow<List<GenerateSongResponse>>(emptyList())
    val clips: StateFlow<List<GenerateSongResponse>> = _clips

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _chords = MutableStateFlow<List<String>>(emptyList())
    val chords: StateFlow<List<String>> = _chords

    fun generateSong(context: Context, request: GenerateSongRequest, key: String, genre: String) {
        val apiKey = Config.getApiKey(context)
        if (apiKey.isBlank()) {
            _error.value = "Please set your API key in Settings"
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _progress.value = 0f
                val response = repository.generateSong(apiKey, request, context) { p ->
                    _progress.value = p
                }
                _audio.value = response
                _clips.value = _clips.value + response
                _chords.value = ChordGenerator.suggest(key, genre)
                _progress.value = 1f
            } catch (e: Exception) {
                _error.value = "Network error—please retry"
                _progress.value = 0f
            }
        }
    }

    fun clearError() { _error.value = null }
    fun mixdownAndExport(context: Context, response: GenerateSongResponse) {
        viewModelScope.launch(Dispatchers.IO) {
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

    fun mixAndExport(context: Context, responses: List<GenerateSongResponse>) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val output = File(context.getExternalFilesDir(null), "musicgen_mix.wav")
                val urls = responses.map { File(it.audioPath).toURI().toString() }
                AudioMixer.mixWavFiles(urls, output)
                _error.value = "Saved to ${output.absolutePath}"
            } catch (e: Exception) {
                _error.value = "Network error—please retry"
            }
        }
    }
}
