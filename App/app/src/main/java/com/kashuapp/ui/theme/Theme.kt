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
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {

    val currentColors = if (darkTheme) DarkKashuLoginScheme else LightKashuLoginScheme

//    val colorScheme = when {
//        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
//            val context = LocalContext.current
//            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
//        }
//
//        darkTheme -> DarkColorScheme
//        else -> LightColorScheme
//    }

    CompositionLocalProvider(LocalKashuColors provides currentColors) {
        MaterialTheme(
            typography = Typography,
            content = content
        )

//    MaterialTheme(
//        colorScheme = valKashuColors,
//        typography = Typography,
//        content = content
//    )
    }
}

