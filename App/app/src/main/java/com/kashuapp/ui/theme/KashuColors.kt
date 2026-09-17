package com.kashuapp.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color


data class KashuColors(
    val mainColor: Color,
    val mainBackground: Color,
    val surface : Color,
    val title : Color,
    val subtitle : Color

)

val LocalKashuColors = staticCompositionLocalOf<KashuColors> {
    error("No se ha provisto ValKashuColors en el tema")
}


object KashuTheme {
    val colors: KashuColors
        @Composable
        @ReadOnlyComposable
        get() = LocalKashuColors.current
}