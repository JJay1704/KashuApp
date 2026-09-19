package com.kashuapp.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color


data class KashuScheme(
    val mainColor: Color,
    val mainBackground: Color,
    val surface : Color,
    val title : Color,
    val subtitle : Color,
    val inputContainer : Color,
    val inputText: Color,

)

val LocalKashuColors = staticCompositionLocalOf<KashuScheme> {
    error("No se ha provisto ValKashuColors en el tema")
}


object KashuTheme {
    val colors: KashuScheme
        @Composable
        @ReadOnlyComposable
        get() = LocalKashuColors.current
}