package com.legendai.musichelper.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Pause
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.legendai.musichelper.GenerateSongRequest
import com.legendai.musichelper.Parameters
import com.legendai.musichelper.MusicViewModel
// Exporting to the app specific external directory does not require
// runtime storage permission, so no permission APIs are needed here.
@Composable
fun MusicScreen(viewModel: MusicViewModel, snackbarHostState: SnackbarHostState) {
    val progress by viewModel.progress.collectAsState()
    val audio by viewModel.audio.collectAsState()
    val chords by viewModel.chords.collectAsState()
    var genre by remember { mutableStateOf("rock") }
    var key by remember { mutableStateOf(TextFieldValue("C")) }
    var tempo by remember { mutableStateOf(120f) }
    var duration by remember { mutableStateOf(30f) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .padding(padding)
                .padding(16.dp)
        ) {
            // Genre Dropdown
            var expanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                TextField(
                    value = genre,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Genre") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor()
                )
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    listOf("rock", "jazz_fusion", "EDM").forEach {
                        DropdownMenuItem(text = { Text(it) }, onClick = {
                            genre = it
                            expanded = false
                        })
                    }
                }
            }
            Spacer(Modifier.height(8.dp))

            // Tempo slider
            Text(text = "Tempo: ${tempo.toInt()}")
            Slider(value = tempo, onValueChange = { tempo = it }, valueRange = 60f..180f)
            Spacer(Modifier.height(8.dp))

            // Duration slider
            Text(text = "Duration: ${duration.toInt()}s")
            Slider(value = duration, onValueChange = { duration = it }, valueRange = 5f..60f)
            Spacer(Modifier.height(8.dp))

            // Key input
            TextField(value = key, onValueChange = { key = it }, label = { Text("Key") })
            Spacer(Modifier.height(8.dp))

            if (chords.isNotEmpty()) {
                Text("Suggested progression: " + chords.joinToString(" - "))
                Spacer(Modifier.height(8.dp))
            }

            Button(onClick = {
                val prompt = buildString {
                    append("A $genre song at ${tempo.toInt()} bpm in the key of ${key.text}.")
                }
                viewModel.generateSong(
                    context = LocalContext.current,
                    request = GenerateSongRequest(
                        inputs = prompt,
                        parameters = Parameters(duration = duration.toInt())
                    ),
                    key = key.text,
                    genre = genre
                )
            }) { Text("Generate Song") }

            if (progress > 0f && progress < 1f) {
                LinearProgressIndicator(progress = progress, modifier = Modifier.fillMaxWidth())
                Text(text = "${(progress * 100).toInt()}%", modifier = Modifier.align(Alignment.CenterHorizontally))
            }

            audio?.let { result ->
                AudioPlayerSection(result)
                Spacer(Modifier.height(8.dp))
                Button(onClick = {
                    viewModel.mixdownAndExport(LocalContext.current, result)
                }) { Text("Export") }
            }
        }
    }
}

@Composable
fun AudioPlayerSection(response: GenerateSongResponse) {
    Column {
        AudioPlayer(url = response.audioPath, label = "Preview")
    }
}

@Composable
fun AudioPlayer(url: String, label: String) {
    val context = LocalContext.current
    var playing by remember { mutableStateOf(false) }
    val player = remember(url) {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(url))
            prepare()
        }
    }
    DisposableEffect(player) {
        onDispose {
            player.release()
        }
    }
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = label, modifier = Modifier.weight(1f))
        IconButton(onClick = {
            playing = !playing
            if (playing) player.play() else player.pause()
        }) {
            Icon(if (playing) Icons.Default.Pause else Icons.Default.PlayArrow, contentDescription = null)
        }
    }
}
