package com.clockworkred.app

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.clockworkred.app.ui.projects.ProjectsScreen

@Composable
fun HomeNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "projects") {
        composable("projects") { ProjectsScreen() }
        composable("editor") { EditorScreen() }
        composable("settings") { SettingsScreen() }
    }
}
