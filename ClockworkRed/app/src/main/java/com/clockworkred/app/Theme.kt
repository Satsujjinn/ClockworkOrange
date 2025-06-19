package com.clockworkred.app

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.core.view.WindowCompat

@Composable
fun ClockworkRedAppTheme(content: @Composable () -> Unit) {
    val colors = lightColorScheme()
    MaterialTheme(
        colorScheme = colors,
        content = content
    )
}
