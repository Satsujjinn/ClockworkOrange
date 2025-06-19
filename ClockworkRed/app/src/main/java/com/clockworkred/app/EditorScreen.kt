package com.clockworkred.app

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun EditorScreen(viewModel: EditorViewModel = hiltViewModel()) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        // TODO: collaborative tab editing canvas
    }
}
