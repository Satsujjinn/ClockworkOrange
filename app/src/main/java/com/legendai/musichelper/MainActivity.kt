package com.legendai.musichelper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import com.legendai.musichelper.ui.MusicScreen
import com.legendai.musichelper.ui.theme.MusicGenTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel: MusicViewModel = viewModel(factory = MusicViewModelFactory)
            MusicGenTheme {
                val snackbarHostState = remember { SnackbarHostState() }
                MusicScreen(viewModel, snackbarHostState)
            }
        }
    }
}
