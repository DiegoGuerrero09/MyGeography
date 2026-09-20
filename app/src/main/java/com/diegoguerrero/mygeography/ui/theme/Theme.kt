package com.diegoguerrero.mygeography.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryBlue,
    onPrimary = DarkBackground,
    primaryContainer = PrimaryCyan,
    onPrimaryContainer = DarkBackground,
    secondary = AccentGold,
    background = DarkBackground,
    surface = DarkCard,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    outline = DarkCardBorder
)

private val DarkTypography = androidx.compose.material3.Typography()

@Composable
fun MyGeographyTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = DarkTypography,
        content = content
    )
}
