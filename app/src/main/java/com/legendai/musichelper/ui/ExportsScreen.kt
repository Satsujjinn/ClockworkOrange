package com.legendai.musichelper.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.icons.Icons
import androidx.compose.material3.icons.filled.ArrowBack
import androidx.compose.material3.icons.filled.Delete
import androidx.compose.material3.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.legendai.musichelper.ExportStore
import kotlinx.coroutines.launch
import android.content.Intent
import androidx.core.content.FileProvider
import java.io.File
import java.text.DateFormat

@Composable
fun ExportsScreen(onDone: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val exports by ExportStore.flow(context).collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Exports") },
                navigationIcon = {
                    IconButton(onClick = onDone) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            items(exports) { entry ->
                val file = File(context.getExternalFilesDir("exports"), entry.fileName)
                Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                    val date = remember(entry.time) {
                        DateFormat.getDateTimeInstance().format(entry.time)
                    }
                    Text(entry.fileName)
                    Text(date, style = MaterialTheme.typography.bodySmall)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        AudioPlayer(file.absolutePath, label = "")
                        IconButton(onClick = {
                            val uri = FileProvider.getUriForFile(
                                context,
                                "${context.packageName}.provider",
                                file
                            )
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "audio/wav"
                                putExtra(Intent.EXTRA_STREAM, uri)
                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            }
                            context.startActivity(Intent.createChooser(intent, null))
                        }) {
                            Icon(Icons.Default.Share, contentDescription = null)
                        }
                        IconButton(onClick = {
                            scope.launch {
                                file.delete()
                                ExportStore.remove(context, entry.fileName)
                            }
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = null)
                        }
                    }
                }
                Divider()
            }
        }
    }
}
