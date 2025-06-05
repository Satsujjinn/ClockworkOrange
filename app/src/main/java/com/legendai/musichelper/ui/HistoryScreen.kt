package com.legendai.musichelper.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.legendai.musichelper.*

@Composable
fun HistoryScreen(viewModel: MusicViewModel, navController: NavController) {
    val context = LocalContext.current
    var tracks by remember { mutableStateOf(emptyList<CachedTrack>()) }

    LaunchedEffect(Unit) {
        tracks = TrackCache.getTracks(context)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("History") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            items(tracks) { track ->
                Column(modifier = Modifier.padding(16.dp)) {
                    AudioPlayer(url = track.path, label = "Created")
                    Spacer(Modifier.height(4.dp))
                    Button(onClick = {
                        viewModel.mixdownAndExport(context, GenerateSongResponse(track.path))
                    }) { Text("Export") }
                }
            }
        }
    }
}
