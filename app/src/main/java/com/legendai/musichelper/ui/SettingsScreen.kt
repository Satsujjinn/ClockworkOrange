package com.legendai.musichelper.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.legendai.musichelper.Config

@Composable
fun SettingsScreen(onDone: () -> Unit) {
    val context = LocalContext.current
    var apiKey by remember { mutableStateOf(Config.getApiKey(context)) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onDone) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(padding)
                .padding(16.dp)
        ) {
            TextField(
                value = apiKey,
                onValueChange = { apiKey = it },
                label = { Text("Hugging Face API Key") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))
            Button(onClick = {
                Config.setApiKey(context, apiKey)
                onDone()
            }) {
                Text("Save")
            }
        }
    }
}
