package com.legendai.musichelper

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.navigation.compose.hiltViewModel
import com.legendai.musichelper.ui.MusicScreen
import com.legendai.musichelper.ui.theme.MusicGenTheme

import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel: MusicViewModel = hiltViewModel()
            MusicGenTheme {
                val snackbarHostState = remember { SnackbarHostState() }
                val error by viewModel.error.collectAsStateWithLifecycle()
                LaunchedEffect(error) {
                    error?.let { snackbarHostState.showSnackbar(it) }
                }
                MusicScreen(viewModel, snackbarHostState)
            }
        }
    }
}
