package com.clockworkred.app

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.clockworkred.app.ui.projects.ProjectsScreen
import com.clockworkred.app.ui.editor.TabEditorScreen
import com.clockworkred.app.ui.arrangement.ArrangementCanvasScreen
import com.clockworkred.app.ui.export.ExportScreen
import com.clockworkred.app.ui.settings.SettingsScreen
import com.clockworkred.app.editor.TabEditorViewModel
import com.clockworkred.domain.model.Instrument
import com.clockworkred.domain.model.PartRequest
import com.clockworkred.domain.model.SongSection
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.LaunchedEffect

@Composable
fun HomeNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "projects") {
        composable("projects") { ProjectsScreen() }
        composable("editor/{instrument}") { backStackEntry ->
            val instrumentName = backStackEntry.arguments?.getString("instrument") ?: "guitar"
            val instrument = runCatching { Instrument.valueOf(instrumentName.uppercase()) }.getOrDefault(Instrument.GUITAR)
            val viewModel: TabEditorViewModel = hiltViewModel()
            LaunchedEffect(instrument) {
                viewModel.requestTab(PartRequest(instrument, "", emptyList(), SongSection.CHORUS))
            }
            TabEditorScreen(viewModel)
        }
        composable("settings") { SettingsScreen() }
        composable("arrangement") { ArrangementCanvasScreen(navController) }
        composable("export") { ExportScreen() }
    }
}
