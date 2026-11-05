package com.kashuapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

private val LightKashuLoginScheme = KashuScheme(
    mainColor = LightGreenPrimary,
    mainBackground = LightBackground,
    surface = LightSurfaceLoging,
    title = LightTitle,
    subtitle = LightSubtitleLogin,
    inputContainer = LightSurfaceLoging,
    inputText = LightTitle
)

private val DarkKashuLoginScheme = KashuScheme(
    mainColor = DarkGreenPrimary,
    mainBackground = DarkBackground,
    surface = DarkSurfaceLogin,
    title = DarkTitle,
    subtitle = DarkSubtitleLogin,
    inputContainer = DarkSurfaceLogin,
    inputText = DarkTitle
)

@Composable
fun KashuAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val currentColors = if (darkTheme) DarkKashuLoginScheme else LightKashuLoginScheme

    CompositionLocalProvider(LocalKashuColors provides currentColors) {
        MaterialTheme(
            typography = Typography, content = content
        )
    }
}
