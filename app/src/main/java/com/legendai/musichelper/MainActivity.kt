package com.legendai.musichelper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.legendai.musichelper.ui.MusicScreen
import com.legendai.musichelper.ui.HistoryScreen
import com.legendai.musichelper.ui.theme.MusicGenTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel: MusicViewModel = viewModel(factory = MusicViewModelFactory)
            MusicGenTheme {
                val snackbarHostState = remember { SnackbarHostState() }
                val error by viewModel.error.collectAsStateWithLifecycle()
                val navController = rememberNavController()
                LaunchedEffect(error) {
                    error?.let { snackbarHostState.showSnackbar(it) }
                }
                NavHost(navController, startDestination = "main") {
                    composable("main") {
                        MusicScreen(viewModel, snackbarHostState) {
                            navController.navigate("history")
                        }
                    }
                    composable("history") {
                        HistoryScreen(viewModel, navController)
                    }
                }
            }
        }
    }
}
