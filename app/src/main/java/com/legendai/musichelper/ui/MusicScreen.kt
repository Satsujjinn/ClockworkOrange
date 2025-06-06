package com.legendai.musichelper.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.List
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.legendai.musichelper.GenerateSongRequest
import com.legendai.musichelper.Parameters
import com.legendai.musichelper.MusicViewModel
import com.legendai.musichelper.util.MelodyGenerator
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
    val genre by viewModel.genre.collectAsState()
    val savedKey by viewModel.key.collectAsState()
    val tempo by viewModel.tempo.collectAsState()
    val duration by viewModel.duration.collectAsState()
    val melody by viewModel.melody.collectAsState()
    val tabs by viewModel.tabs.collectAsState()
    var key by remember { mutableStateOf(TextFieldValue(savedKey)) }
    LaunchedEffect(savedKey) {
        if (savedKey != key.text) key = TextFieldValue(savedKey)
    }
    var selectedClips by remember { mutableStateOf(setOf<String>()) }
    var instrumentExpanded by remember { mutableStateOf(false) }
    var instrument by remember { mutableStateOf(MelodyGenerator.Instrument.GUITAR) }
    var tabInstrumentExpanded by remember { mutableStateOf(false) }
    var tabInstrument by remember { mutableStateOf(MelodyGenerator.Instrument.GUITAR) }
    var search by remember { mutableStateOf(TextFieldValue("")) }

    LaunchedEffect(key.text, genre) {
        viewModel.updateChords(key.text, genre)
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("MusicGen Helper") },
                actions = {
                    IconButton(onClick = onOpenExports) {
                        Icon(Icons.Default.List, contentDescription = null)
                    }
                    IconButton(onClick = onOpenSettings) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = "Settings"
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
                            viewModel.setGenre(it)
                            expanded = false
                        })
                    }
                }
            }
            Spacer(Modifier.height(8.dp))

            // Tempo slider
            Text(text = "Tempo: ${tempo.toInt()}")
            Slider(value = tempo, onValueChange = { viewModel.setTempo(it) }, valueRange = 60f..180f)
            Spacer(Modifier.height(8.dp))

            // Duration slider
            Text(text = "Duration: ${duration.toInt()}s")
            Slider(value = duration.toFloat(), onValueChange = { viewModel.setDuration(it.toInt()) }, valueRange = 5f..60f)
            Spacer(Modifier.height(8.dp))

            // Key input
            TextField(value = key, onValueChange = { key = it; viewModel.setKey(it.text) }, label = { Text("Key") })
            Spacer(Modifier.height(8.dp))

            if (chords.isNotEmpty()) {
                Text("Suggested progression: " + chords.joinToString(" - "))
                Spacer(Modifier.height(8.dp))
            }

            ExposedDropdownMenuBox(expanded = instrumentExpanded, onExpandedChange = { instrumentExpanded = !instrumentExpanded }) {
                TextField(
                    value = instrument.name.lowercase().replaceFirstChar { it.uppercase() },
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Instrument") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = instrumentExpanded) },
                    modifier = Modifier.menuAnchor()
                )
                ExposedDropdownMenu(expanded = instrumentExpanded, onDismissRequest = { instrumentExpanded = false }) {
                    DropdownMenuItem(text = { Text("Guitar") }, onClick = {
                        instrument = MelodyGenerator.Instrument.GUITAR
                        instrumentExpanded = false
                    })
                    DropdownMenuItem(text = { Text("Bass") }, onClick = {
                        instrument = MelodyGenerator.Instrument.BASS
                        instrumentExpanded = false
                    })
                    DropdownMenuItem(text = { Text("Keyboard") }, onClick = {
                        instrument = MelodyGenerator.Instrument.KEYBOARD
                        instrumentExpanded = false
                    })
                }
            }
            Spacer(Modifier.height(8.dp))

            Button(onClick = { viewModel.generateMelody(key.text, instrument) }) { Text("Generate Melody") }

            if (melody.isNotEmpty()) {
                melody.forEach { line ->
                    Text(line, fontFamily = FontFamily.Monospace)
                }
                Spacer(Modifier.height(8.dp))
            }

            TextField(
                value = search,
                onValueChange = { search = it },
                label = { Text("Song search") }
            )
            Spacer(Modifier.height(8.dp))

            ExposedDropdownMenuBox(expanded = tabInstrumentExpanded, onExpandedChange = { tabInstrumentExpanded = !tabInstrumentExpanded }) {
                TextField(
                    value = tabInstrument.name.lowercase().replaceFirstChar { it.uppercase() },
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Tab Instrument") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = tabInstrumentExpanded) },
                    modifier = Modifier.menuAnchor()
                )
                ExposedDropdownMenu(expanded = tabInstrumentExpanded, onDismissRequest = { tabInstrumentExpanded = false }) {
                    DropdownMenuItem(text = { Text("Guitar") }, onClick = {
                        tabInstrument = MelodyGenerator.Instrument.GUITAR
                        tabInstrumentExpanded = false
                    })
                    DropdownMenuItem(text = { Text("Bass") }, onClick = {
                        tabInstrument = MelodyGenerator.Instrument.BASS
                        tabInstrumentExpanded = false
                    })
                    DropdownMenuItem(text = { Text("Keyboard") }, onClick = {
                        tabInstrument = MelodyGenerator.Instrument.KEYBOARD
                        tabInstrumentExpanded = false
                    })
                }
            }
            Spacer(Modifier.height(8.dp))

            Button(onClick = { viewModel.generateTabs(LocalContext.current, search.text, tabInstrument) }) { Text("Generate Tabs") }

            if (tabs.isNotEmpty()) {
                tabs.forEach { line ->
                    Text(line, fontFamily = FontFamily.Monospace)
                }
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
                        parameters = Parameters(duration = duration)
                    ),
                    key = key.text,
                    genre = genre
                )
            }) { Text("Generate Song") }

            if (progress > 0f && progress < 1f) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    LinearProgressIndicator(
                        progress = progress,
                        modifier = Modifier
                            .weight(1f)
                    )
                    Spacer(Modifier.width(8.dp))
                    Button(onClick = { viewModel.cancelGeneration() }) { Text("Cancel") }
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
                        AudioPlayer(url = clip.audioPath, label = "Clip ${index + 1}")
                        Spacer(Modifier.width(8.dp))
                        Button(onClick = {
                            viewModel.mixdownAndExport(LocalContext.current, clip)
                        }) { Text("Export") }
                    }
                    Spacer(Modifier.height(8.dp))
                }
            }

            if (selectedClips.size > 1) {
                Button(onClick = {
                    val selected = clips.filter { selectedClips.contains(it.audioPath) }
                    viewModel.mixAndExport(LocalContext.current, selected)
                }) { Text("Mix & Export") }
            }
        }
    }
}
