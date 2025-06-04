package com.legendai.musichelper.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Pause
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.legendai.musichelper.GenerateSongRequest
import com.legendai.musichelper.MusicViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MusicScreen(viewModel: MusicViewModel, snackbarHostState: SnackbarHostState) {
    val progress by viewModel.progress.collectAsState()
    val stems by viewModel.stems.collectAsState()
    var genre by remember { mutableStateOf("rock") }
    var key by remember { mutableStateOf(TextFieldValue("C")) }
    var tempo by remember { mutableStateOf(120f) }
    var referenceUri by remember { mutableStateOf<Uri?>(null) }

    val filePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        referenceUri = it
    }

    val permission = rememberPermissionState(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
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

            // Reference audio picker
            Button(onClick = { filePicker.launch("audio/*") }) {
                Text(referenceUri?.lastPathSegment ?: "Select Reference Audio")
            }
            Spacer(Modifier.height(8.dp))

            // Tempo slider
            Text(text = "Tempo: ${tempo.toInt()}")
            Slider(value = tempo, onValueChange = { tempo = it }, valueRange = 60f..180f)
            Spacer(Modifier.height(8.dp))

            // Key input
            TextField(value = key, onValueChange = { key = it }, label = { Text("Key") })
            Spacer(Modifier.height(8.dp))

            Button(onClick = {
                viewModel.generateSong(
                    context = LocalContext.current,
                    request = GenerateSongRequest(
                        genre = genre,
                        reference_audio_url = referenceUri?.toString() ?: "",
                        tempo = tempo.toInt(),
                        key = key.text
                    )
                )
            }) { Text("Generate Song") }

            if (progress > 0f && progress < 1f) {
                LinearProgressIndicator(progress = progress, modifier = Modifier.fillMaxWidth())
            }

            stems?.let { result ->
                StemPlayerSection(result)
                Spacer(Modifier.height(8.dp))
                Button(onClick = {
                    if (!permission.status.isGranted) {
                        permission.launchPermissionRequest()
                    } else {
                        viewModel.mixdownAndExport(LocalContext.current, result)
                    }
                }) { Text("Mixdown & Export") }
            }
        }
    }
}

@Composable
fun StemPlayerSection(response: GenerateSongResponse) {
    Column {
        StemPlayer(url = response.synth_url, label = "Synth")
        StemPlayer(url = response.bass_url, label = "Bass")
        StemPlayer(url = response.drums_url, label = "Drums")
        StemPlayer(url = response.solo_wav_url, label = "Solo")
    }
}

@Composable
fun StemPlayer(url: String, label: String) {
    val context = LocalContext.current
    var playing by remember { mutableStateOf(false) }
    val player = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(url))
            prepare()
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
