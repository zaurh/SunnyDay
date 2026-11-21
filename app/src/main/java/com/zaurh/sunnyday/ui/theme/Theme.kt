package com.zaurh.sunnyday.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

private val LightColors = lightColorScheme(
    primary = Sky40,
    onPrimary = Sky99,
    primaryContainer = Sky90,
    onPrimaryContainer = Sky10,
    secondary = Sun40,
    secondaryContainer = Sun90,
    background = Sky99,
    surface = Sky99,
    surfaceContainerHigh = Sky95,
    surfaceContainerHighest = Sky90,
)

private val DarkColors = darkColorScheme(
    primary = Sky80,
    onPrimary = Sky20,
    primaryContainer = Sky30,
    onPrimaryContainer = Sky90,
    secondary = Sun80,
    background = Night10,
    surface = Night10,
    surfaceContainerHigh = Night20,
    surfaceContainerHighest = Night30,
)

private val AppShapes = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(20.dp),
    large = RoundedCornerShape(28.dp),
)

@Composable
fun SunnyDayTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = Typography,
        shapes = AppShapes,
        content = content,
    )
}
