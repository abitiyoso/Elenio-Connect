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

private val ElenioDarkColorScheme = darkColorScheme(
    primary = ElenioElectricBlue,
    onPrimary = Color.White,
    primaryContainer = ElenioBlueContainer,
    onPrimaryContainer = ElenioBlueOnContainer,
    secondary = ElenioBlueAccent,
    onSecondary = Color.White,
    secondaryContainer = ElenioNavyCard,
    onSecondaryContainer = ElenioOffWhite,
    background = ElenioNavyPrimary,
    onBackground = ElenioOffWhite,
    surface = ElenioNavySurface,
    onSurface = ElenioOffWhite,
    surfaceVariant = ElenioNavyCard,
    onSurfaceVariant = Color(0xFFCBD5E1),
    outline = ElenioNavyBorder,
    outlineVariant = Color(0xFF152A47),
    error = ElenioError,
    onError = Color.White,
    errorContainer = ElenioErrorContainer,
    onErrorContainer = Color(0xFFFFDAD6)
)

private val ElenioLightColorScheme = lightColorScheme(
    primary = ElenioElectricBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD6E4FF),
    onPrimaryContainer = ElenioNavyPrimary,
    secondary = Color(0xFF1E3A8A),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFEEF2F6),
    onSecondaryContainer = ElenioDarkText,
    background = ElenioOffWhite,
    onBackground = ElenioDarkText,
    surface = Color.White,
    onSurface = ElenioDarkText,
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = ElenioSecondaryText,
    outline = ElenioBorderLight,
    outlineVariant = Color(0xFFCBD5E1),
    error = ElenioError,
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002)
)

@Composable
fun ElenioConnectTheme(
    darkTheme: Boolean = true, // Default to sleek monitoring dark theme or user preference
    dynamicColor: Boolean = false, // Keep Elenio branding consistent
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> ElenioDarkColorScheme
        else -> ElenioLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
