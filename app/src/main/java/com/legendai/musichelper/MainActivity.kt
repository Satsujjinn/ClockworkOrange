package com.legendai.musichelper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.legendai.musichelper.ui.MusicScreen
import com.legendai.musichelper.ui.SettingsScreen
import com.legendai.musichelper.ui.ExportsScreen
import com.legendai.musichelper.ui.theme.MusicGenTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel: MusicViewModel = viewModel(factory = MusicViewModelFactory)
            val navController = rememberNavController()
            MusicGenTheme {
                val snackbarHostState = remember { SnackbarHostState() }
                val error by viewModel.error.collectAsStateWithLifecycle()
                LaunchedEffect(error) {
                    error?.let { snackbarHostState.showSnackbar(it) }
                }
                NavHost(navController = navController, startDestination = "main") {
                    composable("main") {
                        MusicScreen(
                            viewModel,
                            snackbarHostState,
                            onOpenSettings = { navController.navigate("settings") },
                            onOpenExports = { navController.navigate("exports") }
                        )
                    }
                    composable("settings") {
                        SettingsScreen { navController.popBackStack() }
                    }
                    composable("exports") {
                        ExportsScreen { navController.popBackStack() }
                    }
                }
            }
        }
    }
}
