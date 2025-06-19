package com.clockworkred.app.ui.export

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/** Screen offering export options for an arrangement. */
@Composable
fun ExportScreen() {
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Button(onClick = { /* TODO generate and save PDF to storage */ }, modifier = Modifier.fillMaxWidth()) {
            Text("Download PDF")
        }
        Button(onClick = { /* TODO share generated PDF */ }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
            Text("Share PDF")
        }
        Button(onClick = { /* TODO export MIDI file */ }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
            Text("Export MIDI")
        }
        // TODO request WRITE_EXTERNAL_STORAGE permission when implementing file operations
    }
}
