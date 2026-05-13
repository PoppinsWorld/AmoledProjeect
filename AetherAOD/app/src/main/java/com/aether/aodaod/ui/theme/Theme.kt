package com.aether.aodaod.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = AetherPrimary,
    onPrimary = AetherOnPrimary,
    primaryContainer = AetherPrimaryVariant,
    secondary = AetherSecondary,
    onSecondary = AetherOnSecondary,
    secondaryContainer = AetherSecondaryVariant,
    background = AMOLEDBlack,
    onBackground = TextPrimary,
    surface = AMOLOSurface,
    onSurface = TextPrimary,
    surfaceVariant = AMOLOLightGray,
    onSurfaceVariant = TextSecondary
)

/**
 * Aether AOD Theme
 * AMOLED-optimized dark theme with premium styling
 */
@Composable
fun AetherAODTheme(
    darkTheme: Boolean = true, // Always use dark theme for AMOLED
    dynamicColor: Boolean = false, // Can be enabled for Material You support
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = AMOLEDBlack.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
