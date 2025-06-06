package com.legendai.musichelper.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.List
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.legendai.musichelper.GenerateSongRequest
import com.legendai.musichelper.Parameters
import com.legendai.musichelper.MusicViewModel
import com.legendai.musichelper.R
// Exporting to the app specific external directory does not require
// runtime storage permission, so no permission APIs are needed here.
@Composable
fun MusicScreen(
    viewModel: MusicViewModel,
    snackbarHostState: SnackbarHostState,
    onOpenSettings: () -> Unit = {},
    onOpenExports: () -> Unit = {}
) {
    val progress by viewModel.progress.collectAsState()
    val audio by viewModel.audio.collectAsState()
    val clips by viewModel.clips.collectAsState()
    val chords by viewModel.chords.collectAsState()
    var genre by remember { mutableStateOf("rock") }
    val genreLabels = mapOf(
        "rock" to R.string.genre_rock,
        "jazz_fusion" to R.string.genre_jazz_fusion,
        "EDM" to R.string.genre_edm
    )
    var key by remember { mutableStateOf(TextFieldValue("C")) }
    var tempo by remember { mutableStateOf(120f) }
    var duration by remember { mutableStateOf(30f) }
    var selectedClips by remember { mutableStateOf(setOf<String>()) }

    LaunchedEffect(key.text, genre) {
        viewModel.updateChords(key.text, genre)
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                actions = {
                    IconButton(onClick = onOpenExports) {
                        Icon(Icons.Default.List, contentDescription = null)
                    }
                    IconButton(onClick = onOpenSettings) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = stringResource(R.string.settings_icon_desc)
                        )
                    }
                }
            )
        },
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
                val genreLabel = stringResource(id = genreLabels.getValue(genre))
                TextField(
                    value = genreLabel,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.genre)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor()
                )
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    listOf("rock", "jazz_fusion", "EDM").forEach {
                        DropdownMenuItem(text = { Text(stringResource(id = genreLabels.getValue(it))) }, onClick = {
                            genre = it
                            expanded = false
                        })
                    }
                }
            }
            Spacer(Modifier.height(8.dp))

            // Tempo slider
            Text(text = stringResource(R.string.tempo_label, tempo.toInt()))
            Slider(value = tempo, onValueChange = { tempo = it }, valueRange = 60f..180f)
            Spacer(Modifier.height(8.dp))

            // Duration slider
            Text(text = stringResource(R.string.duration_label, duration.toInt()))
            Slider(value = duration, onValueChange = { duration = it }, valueRange = 5f..60f)
            Spacer(Modifier.height(8.dp))

            // Key input
            TextField(value = key, onValueChange = { key = it }, label = { Text(stringResource(R.string.key)) })
            Spacer(Modifier.height(8.dp))

            if (chords.isNotEmpty()) {
                Text(stringResource(R.string.suggested_progression, chords.joinToString(" - ")))
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
            }) { Text(stringResource(R.string.generate_song)) }

            if (progress > 0f && progress < 1f) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    LinearProgressIndicator(
                        progress = progress,
                        modifier = Modifier
                            .weight(1f)
                    )
                    Spacer(Modifier.width(8.dp))
                    Button(onClick = { viewModel.cancelGeneration() }) { Text(stringResource(R.string.cancel)) }
                }
                Text(
                    text = "${(progress * 100).toInt()}%",
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }

            LazyColumn {
                itemsIndexed(clips) { index, clip ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = selectedClips.contains(clip.audioPath),
                            onCheckedChange = { checked ->
                                selectedClips = if (checked) {
                                    selectedClips + clip.audioPath
                                } else {
                                    selectedClips - clip.audioPath
                                }
                            }
                        )
                        AudioPlayer(url = clip.audioPath, label = stringResource(R.string.clip_label, index + 1))
                        Spacer(Modifier.width(8.dp))
                        Button(onClick = {
                            viewModel.mixdownAndExport(LocalContext.current, clip)
                        }) { Text(stringResource(R.string.export)) }
                    }
                    Spacer(Modifier.height(8.dp))
                }
            }

            if (selectedClips.size > 1) {
                Button(onClick = {
                    val selected = clips.filter { selectedClips.contains(it.audioPath) }
                    viewModel.mixAndExport(LocalContext.current, selected)
                }) { Text(stringResource(R.string.mix_export)) }
            }
        }
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
