package com.clockworkred.app.ui.export

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.res.stringResource
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.clockworkred.app.R

/** Screen offering export options for an arrangement. */
@Composable
fun ExportScreen() {
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Button(onClick = { /* TODO generate and save PDF to storage */ }, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(id = R.string.download_pdf))
        }
        Button(onClick = { /* TODO share generated PDF */ }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
            Text(stringResource(id = R.string.share_pdf))
        }
        Button(onClick = { /* TODO export MIDI file */ }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
            Text(stringResource(id = R.string.export_midi))
        }
        // TODO request WRITE_EXTERNAL_STORAGE permission when implementing file operations
    }
}
