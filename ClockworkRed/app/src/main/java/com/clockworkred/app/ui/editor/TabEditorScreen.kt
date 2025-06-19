package com.clockworkred.app.ui.editor

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.clockworkred.app.editor.TabEditorViewModel
import com.clockworkred.domain.model.Instrument
import com.clockworkred.domain.model.PartRequest
import com.clockworkred.domain.model.SongSection
import androidx.navigation.NavHostController
import com.clockworkred.domain.model.TheoryTopic

@Composable
fun TabEditorScreen(
    navController: NavHostController,
    viewModel: TabEditorViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val instrument = remember { mutableStateOf(Instrument.GUITAR) }
    val instrumentExpanded = remember { mutableStateOf(false) }
    val style = remember { mutableStateOf("") }
    val references = remember { mutableStateOf("") }
    val section = remember { mutableStateOf(SongSection.CHORUS) }
    val sectionExpanded = remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Instrument dropdown
        ExposedDropdownMenuBox(expanded = instrumentExpanded.value, onExpandedChange = { instrumentExpanded.value = !instrumentExpanded.value }) {
            OutlinedTextField(
                value = instrument.value.name.lowercase().replaceFirstChar { it.uppercaseChar() },
                onValueChange = {},
                readOnly = true,
                label = { Text("Instrument") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = instrumentExpanded.value) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = instrumentExpanded.value, onDismissRequest = { instrumentExpanded.value = false }) {
                Instrument.values().forEach { option ->
                    DropdownMenuItem(text = { Text(option.name.lowercase().replaceFirstChar { it.uppercaseChar() }) }, onClick = {
                        instrument.value = option
                        instrumentExpanded.value = false
                    })
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        // Style text field
        OutlinedTextField(
            value = style.value,
            onValueChange = { style.value = it },
            label = { Text("Style") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = references.value,
            onValueChange = { references.value = it },
            label = { Text("Reference Songs (comma separated)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Section dropdown
        ExposedDropdownMenuBox(expanded = sectionExpanded.value, onExpandedChange = { sectionExpanded.value = !sectionExpanded.value }) {
            OutlinedTextField(
                value = section.value.name.lowercase().replaceFirstChar { it.uppercaseChar() },
                onValueChange = {},
                readOnly = true,
                label = { Text("Section") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = sectionExpanded.value) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = sectionExpanded.value, onDismissRequest = { sectionExpanded.value = false }) {
                SongSection.values().forEach { option ->
                    DropdownMenuItem(text = { Text(option.name.lowercase().replaceFirstChar { it.uppercaseChar() }) }, onClick = {
                        section.value = option
                        sectionExpanded.value = false
                    })
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            viewModel.requestTab(
                PartRequest(
                    instrument.value,
                    style.value,
                    references.value.split(',').map { it.trim() }.filter { it.isNotEmpty() },
                    section.value
                )
            )
        }) {
            Text("Generate")
        }
        Spacer(modifier = Modifier.height(16.dp))

        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Text(uiState.tabText)
                uiState.theoryNotes?.let {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(it)
                    TextButton(onClick = { navController.navigate("theory/${TheoryTopic.MODE.name.lowercase()}") }) {
                        Text("Theory Help")
                    }
                }
            }
        }
    }
}
