package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DesertColorScheme = darkColorScheme(
    primary = DesertGold,
    onPrimary = DesertObsidian,
    primaryContainer = DesertGoldDark,
    onPrimaryContainer = DesertSandLight,
    secondary = OasisTeal,
    onSecondary = Color.White,
    tertiary = DesertCrimson,
    background = DesertObsidian,
    onBackground = DesertSandLight,
    surface = Color(0xFF26180B),
    onSurface = DesertSandLight,
    surfaceVariant = Color(0xFF3B2513),
    onSurfaceVariant = DesertGold
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DesertColorScheme,
        typography = Typography,
        content = content
    )
}
