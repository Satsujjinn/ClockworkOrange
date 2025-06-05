package com.legendai.musichelper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.legendai.musichelper.ui.MusicScreen
import com.legendai.musichelper.ui.SettingsScreen
import com.legendai.musichelper.ui.theme.MusicGenTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel: MusicViewModel = viewModel(factory = MusicViewModelFactory)
            var showSettings by remember { mutableStateOf(false) }
            MusicGenTheme {
                val snackbarHostState = remember { SnackbarHostState() }
                val error by viewModel.error.collectAsStateWithLifecycle()
                LaunchedEffect(error) {
                    error?.let { snackbarHostState.showSnackbar(it) }
                }
                if (showSettings) {
                    SettingsScreen { showSettings = false }
                } else {
                    MusicScreen(viewModel, snackbarHostState) { showSettings = true }
                }
            }
        }
    }
}
