package com.clockworkred.app.ui.projects

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.ui.res.stringResource
import com.clockworkred.app.projects.ProjectsUiState
import com.clockworkred.app.projects.ProjectsViewModel
import androidx.navigation.NavHostController
import androidx.compose.material3.MaterialTheme
import com.clockworkred.app.R

/** Displays list of projects. */
@Composable
fun ProjectsScreen(
    navController: NavHostController,
    viewModel: ProjectsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { if (!uiState.isCreating) showDialog = false },
            title = { Text(stringResource(id = R.string.new_project)) },
            text = {
                Column {
                    TextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text(stringResource(id = R.string.project_name)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    uiState.creationError?.let {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(it, color = MaterialTheme.colorScheme.error)
                    }
                    if (uiState.isCreating) {
                        Spacer(modifier = Modifier.height(8.dp))
                        CircularProgressIndicator(modifier = Modifier.size(16.dp))
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { viewModel.createProject(name) }, enabled = !uiState.isCreating) {
                    Text(stringResource(id = R.string.create))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }, enabled = !uiState.isCreating) {
                    Text(stringResource(id = R.string.cancel))
                }
            }
        )
    }

    LaunchedEffect(uiState.isCreating, uiState.projects) {
        if (showDialog && !uiState.isCreating && uiState.creationError == null) {
            showDialog = false
            uiState.projects.lastOrNull()?.let { navController.navigate("editor/${'$'}{it.id}") }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            uiState.isLoading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            uiState.projects.isEmpty() -> {
                Column(modifier = Modifier.align(Alignment.Center)) {
                    Text(stringResource(id = R.string.no_projects))
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { showDialog = true }) {
                        Text(stringResource(id = R.string.new_project))
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
