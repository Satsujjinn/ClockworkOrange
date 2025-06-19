package com.clockworkred.app.ui.arrangement

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.clockworkred.app.arrangement.ArrangementViewModel
import com.clockworkred.domain.model.SongSectionOrder

@Composable
fun ArrangementCanvasScreen(
    navController: NavHostController,
    viewModel: ArrangementViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            LazyColumn(modifier = Modifier.weight(1f)) {
                itemsIndexed(uiState.structure.sections, key = { _, item -> item.section.name }) { index, item: SongSectionOrder ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Icon(Icons.Default.DragHandle, contentDescription = "Drag")
                        // TODO implement real drag and drop reordering
                        Text(
                            text = item.section.name.lowercase().replaceFirstChar { it.uppercaseChar() },
                            modifier = Modifier.weight(1f).padding(start = 8.dp)
                        )
                        IconButton(onClick = { viewModel.moveSection(index, index - 1) }) {
                            Icon(Icons.Default.ArrowUpward, contentDescription = "Move Up")
                        }
                        IconButton(onClick = { viewModel.moveSection(index, index + 1) }) {
                            Icon(Icons.Default.ArrowDownward, contentDescription = "Move Down")
                        }
                    }
                }
            }
            Button(
                onClick = { viewModel.saveArrangement() },
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Text("Save Arrangement")
            }
        }
        if (uiState.isSaving) {
            Box(modifier = Modifier.matchParentSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        FloatingActionButton(
            onClick = { navController.navigate("export") },
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
        ) {
            Icon(Icons.Default.Share, contentDescription = "Export")
        }
    }
}
