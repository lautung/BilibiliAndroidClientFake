package com.bilibili.client.core.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import com.bilibili.client.ui.settings.SettingsViewModel

// Bilibili brand colors
val BiliPink = Color(0xFFFB7299)
val BiliPinkDark = Color(0xFFE55B8A)
val BiliBlue = Color(0xFF00A1D6)
val BiliGray = Color(0xFFF4F4F4)
val BiliDarkSurface = Color(0xFF1E1E1E)
val BiliDarkBackground = Color(0xFF141414)

private val LightColorScheme = lightColorScheme(
    primary = BiliPink,
    onPrimary = Color.White,
    secondary = BiliBlue,
    background = Color(0xFFF5F5F5),
    surface = Color.White,
    surfaceVariant = Color(0xFFF0F0F0),
    onBackground = Color(0xFF1A1A1A),
    onSurface = Color(0xFF1A1A1A),
    onSurfaceVariant = Color(0xFF666666),
    outline = Color(0xFFE0E0E0),
)

private val DarkColorScheme = darkColorScheme(
    primary = BiliPink,
    onPrimary = Color.White,
    secondary = BiliBlue,
    background = BiliDarkBackground,
    surface = BiliDarkSurface,
    surfaceVariant = Color(0xFF2A2A2A),
    onBackground = Color(0xFFE0E0E0),
    onSurface = Color(0xFFE0E0E0),
    onSurfaceVariant = Color(0xFF999999),
    outline = Color(0xFF333333),
)

@Composable
fun BilibiliTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
