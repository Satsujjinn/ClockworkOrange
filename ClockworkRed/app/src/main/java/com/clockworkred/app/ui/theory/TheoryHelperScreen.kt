package com.clockworkred.app.ui.theory

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.clockworkred.app.theory.TheoryViewModel
import com.clockworkred.app.theory.TheoryUiState
import com.clockworkred.domain.model.TheoryTopic

@Composable
fun TheoryHelperScreen(
    viewModel: TheoryViewModel = hiltViewModel(),
    topic: TheoryTopic
) {
    LaunchedEffect(topic) { viewModel.loadTopic(topic) }
    val uiState by viewModel.uiState.collectAsState()

    when {
        uiState.isLoading -> {
            CircularProgressIndicator()
        }
        uiState.error != null -> {
            Snackbar { Text(uiState.error!!) }
        }
        uiState.note != null -> {
            val note = uiState.note
            Column(modifier = Modifier.padding(16.dp)) {
                Text(note.title, style = MaterialTheme.typography.headlineSmall)
                Text(note.description, style = MaterialTheme.typography.bodyMedium)
                LazyColumn(modifier = Modifier.padding(top = 8.dp)) {
                    items(note.examples) { example ->
                        Text("- $example", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}
