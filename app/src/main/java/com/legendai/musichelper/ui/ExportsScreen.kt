package com.legendai.musichelper.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import com.legendai.musichelper.ExportStore
import android.content.Intent
import androidx.core.content.FileProvider
import java.io.File
import java.text.DateFormat

@Composable
fun ExportsScreen(onDone: () -> Unit) {
    val context = LocalContext.current
    var exports by remember { mutableStateOf(ExportStore.list(context)) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.exports)) },
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
                            file.delete()
                            ExportStore.remove(context, entry.fileName)
                            exports = ExportStore.list(context)
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
