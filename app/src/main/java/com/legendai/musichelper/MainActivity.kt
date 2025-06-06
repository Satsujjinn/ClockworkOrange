package com.legendai.musichelper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.legendai.musichelper.ui.ExportsScreen
import com.legendai.musichelper.ui.MusicScreen
import com.legendai.musichelper.ui.SettingsScreen
import com.legendai.musichelper.ui.theme.MusicGenTheme

sealed class Destination(val route: String, val icon: ImageVector, val label: String) {
    object Create : Destination("create", Icons.Default.MusicNote, "Create")
    object Exports : Destination("exports", Icons.Default.List, "Exports")
    object Settings : Destination("settings", Icons.Default.Settings, "Settings")
}

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
                    error?.let {
                        snackbarHostState.showSnackbar(it)
                        viewModel.clearError()
                    }
                }
                val items = listOf(Destination.Create, Destination.Exports, Destination.Settings)
                Scaffold(
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                    bottomBar = {
                        NavigationBar {
                            val currentBackStackEntry by navController.currentBackStackEntryAsState()
                            val currentRoute = currentBackStackEntry?.destination?.route
                            items.forEach { dest ->
                                NavigationBarItem(
                                    selected = currentRoute == dest.route,
                                    onClick = {
                                        navController.navigate(dest.route) {
                                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                    icon = { Icon(dest.icon, contentDescription = dest.label) },
                                    label = { Text(dest.label) }
                                )
                            }
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.background
                ) { padding ->
                    NavHost(
                        navController = navController,
                        startDestination = Destination.Create.route,
                        modifier = Modifier.padding(padding)
                    ) {
                        composable(Destination.Create.route) { MusicScreen(viewModel, snackbarHostState) }
                        composable(Destination.Settings.route) { SettingsScreen() }
                        composable(Destination.Exports.route) { ExportsScreen() }
                    }
                }
            }
        }
    }
}
