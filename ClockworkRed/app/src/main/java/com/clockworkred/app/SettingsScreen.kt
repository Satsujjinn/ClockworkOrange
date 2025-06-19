package com.clockworkred.app

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SettingsScreen(viewModel: SettingsViewModel = hiltViewModel()) {
    val apiKey by viewModel.apiKey.collectAsState("")

    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = apiKey,
            onValueChange = { viewModel.saveApiKey(it) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("API Key") }
        )
        Button(onClick = { viewModel.saveApiKey(apiKey) }, modifier = Modifier.padding(top = 8.dp)) {
            Text("Save")
        }
    }
}
