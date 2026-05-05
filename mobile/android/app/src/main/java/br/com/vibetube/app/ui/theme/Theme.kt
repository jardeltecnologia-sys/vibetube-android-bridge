package br.com.vibetube.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val VibeColorScheme = darkColorScheme(
    primary = VibeRed,
    onPrimary = Color.White,
    primaryContainer = VibeRedDark,
    onPrimaryContainer = Color.White,

    secondary = VibeRedLight,
    onSecondary = Color.White,

    background = VibeBlack,
    onBackground = VibeTextPrimary,

    surface = VibeSurface,
    onSurface = VibeTextPrimary,
    surfaceVariant = VibeSurfaceVariant,
    onSurfaceVariant = VibeTextSecondary,

    outline = VibeOutline,
    error = VibeError,
    onError = VibeOnError
)

@Composable
fun VibeTubeTheme(
    @Suppress("UNUSED_PARAMETER") darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Sempre tema escuro — VibeTube é um app de vídeo vertical em fundo preto.
    val colorScheme = VibeColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = VibeBlack.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = VibeTypography,
        content = content
    )
}
