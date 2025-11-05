package com.example.stocksandbox.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColors = lightColorScheme(
    primary = StockBlue,
    onPrimary = Color.White,
    primaryContainer = StockBlueLight,
    onPrimaryContainer = Color(0xFF001D36),
    secondary = Color(0xFF5F6368),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE8EAED),
    onSecondaryContainer = Color(0xFF202124),
    tertiary = Color(0xFF018786),
    onTertiary = Color.White,
    background = Color(0xFFF8F9FA),
    onBackground = Color(0xFF202124),
    surface = Color.White,
    onSurface = Color(0xFF202124)
)

private val DarkColors = darkColorScheme(
    primary = StockBlueLight,
    onPrimary = Color(0xFF001D36),
    primaryContainer = StockBlueDark,
    onPrimaryContainer = Color.White,
    secondary = Color(0xFFBDC1C6),
    onSecondary = Color(0xFF202124),
    secondaryContainer = Color(0xFF3C4043),
    onSecondaryContainer = Color(0xFFE8EAED),
    tertiary = Color(0xFF03DAC6),
    onTertiary = Color(0xFF00332E),
    background = Color(0xFF131314),
    onBackground = Color(0xFFE8EAED),
    surface = Color(0xFF1F1F1F),
    onSurface = Color(0xFFE8EAED)
)

@Composable
fun StockSandboxTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColors
        else -> LightColors
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
