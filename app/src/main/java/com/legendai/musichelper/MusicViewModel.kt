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
import com.legendai.musichelper.util.MelodyGenerator
import com.legendai.musichelper.util.TabGenerator
import com.legendai.musichelper.UserPreferencesRepository
import com.legendai.musichelper.spotify.SpotifyService

// ViewModel handling business logic and exposing Compose states
class MusicViewModel(
    private val repository: MusicRepository,
    private val prefs: UserPreferencesRepository,
    private val spotify: SpotifyService,
) : ViewModel() {

    private val _progress = MutableStateFlow(0f)
    val progress: StateFlow<Float> = _progress

    private val _audio = MutableStateFlow<GenerateSongResponse?>(null)
    val audio: StateFlow<GenerateSongResponse?> = _audio

    private val _clips = MutableStateFlow<List<GenerateSongResponse>>(emptyList())
    val clips: StateFlow<List<GenerateSongResponse>> = _clips

    private val _genre = MutableStateFlow("rock")
    val genre: StateFlow<String> = _genre

    private val _keyPref = MutableStateFlow("C")
    val key: StateFlow<String> = _keyPref

    private val _tempo = MutableStateFlow(120f)
    val tempo: StateFlow<Float> = _tempo

    private val _duration = MutableStateFlow(30)
    val duration: StateFlow<Int> = _duration

    private val _melody = MutableStateFlow<List<String>>(emptyList())
    val melody: StateFlow<List<String>> = _melody

    private val _tabs = MutableStateFlow<List<String>>(emptyList())
    val tabs: StateFlow<List<String>> = _tabs

    init {
        viewModelScope.launch {
            val stored = prefs.getPreferences()
            _genre.value = stored.genre
            _keyPref.value = stored.key
            _tempo.value = stored.tempo
            _duration.value = stored.duration
        }
    }

    fun setGenre(value: String) {
        _genre.value = value
        viewModelScope.launch { prefs.setGenre(value) }
    }

    fun setKey(value: String) {
        _keyPref.value = value
        viewModelScope.launch { prefs.setKey(value) }
    }

    fun setTempo(value: Float) {
        _tempo.value = value
        viewModelScope.launch { prefs.setTempo(value) }
    }

    fun setDuration(value: Int) {
        _duration.value = value
        viewModelScope.launch { prefs.setDuration(value) }
    }

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _chords = MutableStateFlow<List<String>>(emptyList())
    val chords: StateFlow<List<String>> = _chords

    fun updateChords(key: String, genre: String) {
        _chords.value = ChordGenerator.suggest(key, genre)
    }

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
                updateChords(key, genre)
                _progress.value = 1f
            } catch (e: Exception) {
                _error.value = "Network error—please retry"
                _progress.value = 0f
            }
        }
    }

    fun cancelGeneration() {
        repository.cancel()
        _progress.value = 0f
    }

    fun clearError() { _error.value = null }

    fun generateMelody(key: String, instrument: MelodyGenerator.Instrument) {
        _melody.value = MelodyGenerator.generate(key, instrument = instrument)
    }

    fun generateTabs(context: Context, query: String, instrument: MelodyGenerator.Instrument) {
        val key = Config.getSpotifyApiKey(context)
        if (key.isBlank()) {
            _error.value = "Please set your Spotify API key in Settings"
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val track = spotify.searchAndAnalyze(key, query)
                if (track == null) {
                    _error.value = "Track not found"
                    return@launch
                }
                _tabs.value = TabGenerator.generate(track, instrument)
            } catch (e: Exception) {
                _error.value = "Spotify error"
            }
        }
    }
    fun mixdownAndExport(context: Context, response: GenerateSongResponse) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val exportsDir = context.getExternalFilesDir("exports")
                if (exportsDir == null) {
                    _error.value = "Storage unavailable"
                    return@launch
                }
                exportsDir.mkdirs()

                val input = File(response.audioPath)
                val fileName = "musicgen_${System.currentTimeMillis()}.wav"
                val output = File(exportsDir, fileName)
                input.copyTo(output, overwrite = true)
                ExportStore.add(context, fileName)
                _error.value = "Saved to ${output.absolutePath}"
            } catch (e: Exception) {
                _error.value = "File error—please retry"
            }
        }
    }

    fun mixAndExport(context: Context, responses: List<GenerateSongResponse>) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val baseDir = context.getExternalFilesDir(null)
                if (baseDir == null) {
                    _error.value = "Storage unavailable"
                    return@launch
                }

                val output = File(baseDir, "musicgen_mix.wav")
                val urls = responses.map { File(it.audioPath).toURI().toString() }
                AudioMixer.mixWavFiles(urls, output)
                _error.value = "Saved to ${output.absolutePath}"
            } catch (e: Exception) {
                _error.value = "File error—please retry"
            }
        }
    }
}
