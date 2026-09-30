package com.kashuapp.core.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
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