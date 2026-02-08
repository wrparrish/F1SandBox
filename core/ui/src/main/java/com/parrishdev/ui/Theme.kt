package com.parrishdev.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = F1Red,
    onPrimary = ChequeredWhite,
    primaryContainer = F1RedDark,
    onPrimaryContainer = ChequeredWhite,
    secondary = PitlaneGrey,
    onSecondary = ChequeredWhite,
    secondaryContainer = BarrierGrey,
    onSecondaryContainer = ChequeredWhite,
    tertiary = FastestLapPurple,
    onTertiary = ChequeredWhite,
    background = Carbon,
    onBackground = ChequeredWhite,
    surface = Asphalt,
    onSurface = ChequeredWhite,
    surfaceVariant = Pitwall,
    onSurfaceVariant = SilverText,
    outline = GhostGrey,
    outlineVariant = PitlaneGrey,
    error = DNFRed,
    onError = ChequeredWhite,
    inverseSurface = ChequeredWhite,
    inverseOnSurface = Carbon,
    surfaceContainerLowest = Carbon,
    surfaceContainerLow = Color(0xFF0F0F1A),
    surfaceContainer = Asphalt,
    surfaceContainerHigh = Pitwall,
    surfaceContainerHighest = PitlaneGrey,
)

private val LightColorScheme = lightColorScheme(
    primary = F1Red,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFDAD6),
    onPrimaryContainer = Color(0xFF410002),
    secondary = Color(0xFF5A5A6E),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE0E0F0),
    onSecondaryContainer = Color(0xFF1A1A2E),
    tertiary = FastestLapPurple,
    onTertiary = Color.White,
    background = LightBackground,
    onBackground = LightOnSurface,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceVariant,
    outline = Color(0xFF8A8A9E),
    error = DNFRed,
    onError = Color.White,
)

@Composable
fun F1SandboxTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
