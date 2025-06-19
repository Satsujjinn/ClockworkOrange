package com.clockworkred.app.ui.projects

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import com.clockworkred.app.projects.ProjectsUiState
import com.clockworkred.app.projects.ProjectsViewModel

/** Displays list of projects. */
@Composable
fun ProjectsScreen(viewModel: ProjectsViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            uiState.isLoading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            uiState.projects.isEmpty() -> {
                Column(modifier = Modifier.align(Alignment.Center)) {
                    Text("No projects yet")
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { /* TODO new project */ }) {
                        Text("New Project")
                    }
                }
            }
            else -> {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(uiState.projects) { project ->
                        Text(project.name, modifier = Modifier.padding(16.dp))
                    }
                }
            }
        }
    }
}
