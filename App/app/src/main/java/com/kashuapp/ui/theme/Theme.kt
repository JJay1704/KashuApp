package com.kashuapp.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext




private val LightKashuLoginScheme= KashuScheme(


    mainColor = LightGreenPrimary,
    mainBackground =  LightBackground,
    surface = LightSurfaceLoging,
            title = LightTitle,
    subtitle = LightSubtitleLogin,
    inputContainer = Color.Red,
    inputText = Color.Blue

    )



private val DarkKashuLoginScheme = KashuScheme(

    mainColor = DarkGreenPrimary,
    mainBackground =  DarkBackground,
    surface = DarkSurfaceLogin,
    title = DarkTitle,
    subtitle = DarkSubtitleLogin,
    inputContainer = Color.Red,
    inputText = Color.Blue



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
            typography = Typography,
            content = content
        )
    }
}

