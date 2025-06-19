package com.clockworkred.app

import android.Manifest
import android.content.Intent
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.Environment
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.core.content.FileProvider
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions
import com.clockworkred.app.BuildConfig
import java.io.File
import java.io.FileOutputStream

@AndroidEntryPoint
@RuntimePermissions
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ClockworkRedAppTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()
                    AppNavGraph(navController)
                }
            }
        }
    }

    private var pdfFile: File? = null
    private var midiFile: File? = null

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun savePdf() {
        val docs = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        val file = File(docs, "arrangement.pdf")
        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(300, 300, 1).create()
        val page = document.startPage(pageInfo)
        val paint = Paint()
        page.canvas.drawText("Tab export", 10f, 25f, paint)
        document.finishPage(page)
        FileOutputStream(file).use { out -> document.writeTo(out) }
        document.close()
        pdfFile = file
    }

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun sharePdf() {
        if (pdfFile == null || !(pdfFile?.exists() ?: false)) {
            savePdf()
        }
        pdfFile?.let { shareFile(it, "application/pdf") }
    }

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun exportMidi() {
        val music = getExternalFilesDir(Environment.DIRECTORY_MUSIC)
        val file = File(music, "arrangement.mid")
        FileOutputStream(file).use { fos ->
            fos.write(byteArrayOf(0x4d, 0x54, 0x68, 0x64))
        }
        midiFile = file
        shareFile(file, "audio/midi")
    }

    private fun shareFile(file: File, mime: String) {
        val uri = FileProvider.getUriForFile(
            this,
            BuildConfig.APPLICATION_ID + ".provider",
            file
        )
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = mime
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(Intent.createChooser(intent, null))
    }
}
