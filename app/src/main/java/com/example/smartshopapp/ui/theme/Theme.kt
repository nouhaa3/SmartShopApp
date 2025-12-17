package com.example.smartshopapp.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = OldRose,
    secondary = AmethystSmoke,
    tertiary = DustyLavender,
    background = RosyTaupe.copy(alpha = 0.05f),
    surface = RosyTaupe.copy(alpha = 0.08f),
    onPrimary = androidx.compose.ui.graphics.Color.White,
    onBackground = SpaceIndigo,
    onSurface = SpaceIndigo
)

private val DarkColorScheme = darkColorScheme(
    primary = OldRose,
    secondary = AmethystSmoke,
    tertiary = DustyLavender,
    background = SpaceIndigo,
    surface = SpaceIndigo.copy(alpha = 0.9f),
    onPrimary = androidx.compose.ui.graphics.Color.White,
    onBackground = RosyTaupe,
    onSurface = RosyTaupe
)

@Composable
fun SmartShopAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
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
