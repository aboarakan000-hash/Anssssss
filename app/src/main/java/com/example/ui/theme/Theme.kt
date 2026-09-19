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

private val CyberDarkColorScheme = darkColorScheme(
    primary = CyberEmerald,
    onPrimary = Color(0xFF003816),
    primaryContainer = Color(0xFF005324),
    onPrimaryContainer = Color(0xFF6BFF97),
    
    secondary = CyberCyan,
    onSecondary = Color(0xFF00363D),
    secondaryContainer = Color(0xFF004F58),
    onSecondaryContainer = Color(0xFF70F5FF),
    
    tertiary = CyberPurple,
    onTertiary = Color(0xFF27005D),
    tertiaryContainer = Color(0xFF42008E),
    onTertiaryContainer = Color(0xFFD6BAFF),
    
    background = CyberDarkBg,
    onBackground = TextPrimary,
    surface = CyberSurface,
    onSurface = TextPrimary,
    surfaceVariant = CyberSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = CyberCardBorder,
    outlineVariant = Color(0xFF1E2D42),
    
    error = SeverityCritical,
    onError = Color.White
)

private val CyberLightColorScheme = lightColorScheme(
    primary = Color(0xFF008744),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFB8F5CE),
    onPrimaryContainer = Color(0xFF00210B),
    
    secondary = Color(0xFF007A8A),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFBCEBEE),
    onSecondaryContainer = Color(0xFF001F24),
    
    tertiary = Color(0xFF5A25B8),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFE8DDFF),
    onTertiaryContainer = Color(0xFF1A004C),
    
    background = Color(0xFFF4F7FA),
    onBackground = Color(0xFF111827),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF111827),
    surfaceVariant = Color(0xFFE2E8F0),
    onSurfaceVariant = Color(0xFF475569),
    outline = Color(0xFFCBD5E1),
    outlineVariant = Color(0xFFE2E8F0),
    
    error = SeverityCritical,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to sleek cyber dark theme
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> CyberDarkColorScheme
        else -> CyberLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
