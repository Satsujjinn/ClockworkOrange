package com.legendai.musichelper.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.material3.Typography
import android.os.Build

private val WoodBrown = Color(0xFF8B4513)
private val RugRed = Color(0xFF8B0000)
private val Cream = Color(0xFFF5F5DC)

private val LightColors = lightColorScheme(
    primary = RugRed,
    secondary = WoodBrown,
    background = Cream,
    surface = Cream,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color.Black,
    onSurface = Color.Black
)

private val DarkColors = darkColorScheme(
    primary = RugRed,
    secondary = WoodBrown,
    background = Color(0xFF3E2723),
    surface = Color(0xFF3E2723),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White
)

private val LegendTypography = Typography(defaultFontFamily = FontFamily.Cursive)

@Composable
fun MusicGenTheme(content: @Composable () -> Unit) {
    val darkTheme = isSystemInDarkTheme()
    val context = LocalContext.current
    val colorScheme = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColors
        else -> LightColors
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = LegendTypography,
        content = content
    )
}
