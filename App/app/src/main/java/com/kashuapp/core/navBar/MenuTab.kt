package com.kashuapp.core.navBar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SwipeUpAlt
import androidx.compose.ui.graphics.vector.ImageVector

sealed class MenuTab(
    val title: String,
    val icon: ImageVector
) {
    data object Categories : MenuTab(
        title = "Categorías",
        icon = Icons.Default.Category
    )
    data object Profile : MenuTab(
        title = "Mi Perfil",
        icon = Icons.Default.Person
    )
    data object Settings : MenuTab(
        title = "Configuración",
        icon = Icons.Default.Settings
    )
    companion object {
        val tabs: List<MenuTab>
            get() = listOf(Categories, Profile, Settings)
    }
}