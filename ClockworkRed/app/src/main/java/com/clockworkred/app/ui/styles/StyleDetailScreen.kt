package com.clockworkred.app.ui.styles

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.clockworkred.app.styles.StyleDetailViewModel

@Composable
fun StyleDetailScreen(
    navController: NavHostController,
    styleId: String,
    viewModel: StyleDetailViewModel = hiltViewModel()
) {
    LaunchedEffect(styleId) { viewModel.loadStyle(styleId) }
    val uiState by viewModel.uiState.collectAsState()

    when {
        uiState.isLoading -> CircularProgressIndicator()
        uiState.error != null -> Text(uiState.error!!)
        uiState.style != null -> {
            val style = uiState.style
            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                Text(style.name)
                Text(style.description)
                Spacer(modifier = Modifier.height(8.dp))
                LazyColumn(modifier = Modifier.weight(1f)) {
                    item { Text("Characteristics:") }
                    items(style.characteristics) { Text("- $it") }
                    item { Spacer(Modifier.height(8.dp)) ; Text("Instrumentation:") }
                    items(style.instrumentation) { Text("- $it") }
                    item { Spacer(Modifier.height(8.dp)); Text("Example Songs:") }
                    items(style.exampleSongs) { Text("- $it") }
                }
                Button(onClick = { navController.navigate("editor/guitar?style=${'$'}{style.id}") }) {
                    Text("Use This Style")
                }
            }
        }
    }
}
