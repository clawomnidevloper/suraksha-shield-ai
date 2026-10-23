package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = CyberBluePrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF003D82),
    onPrimaryContainer = Color(0xFFD6E4FF),
    secondary = CyberBlueSecondary,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF004D47),
    onSecondaryContainer = Color(0xFFA7F3D0),
    tertiary = CyberAccentPurple,
    background = DarkCyberBackground,
    onBackground = Color(0xFFE2E8F0),
    surface = DarkCyberSurface,
    onSurface = Color(0xFFF1F5F9),
    surfaceVariant = DarkCyberSurfaceVariant,
    onSurfaceVariant = Color(0xFFCBD5E1),
    error = AlertRed,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = LightCyberPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD8E6FF),
    onPrimaryContainer = Color(0xFF001A41),
    secondary = LightCyberSecondary,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFC7F3EA),
    onSecondaryContainer = Color(0xFF00201D),
    tertiary = CyberAccentPurple,
    background = LightCyberBackground,
    onBackground = Color(0xFF0F172A),
    surface = LightCyberSurface,
    onSurface = Color(0xFF0F172A),
    surfaceVariant = LightCyberSurfaceVariant,
    onSurfaceVariant = Color(0xFF475569),
    error = AlertRed,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Use our bespoke cybersecurity theme
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
