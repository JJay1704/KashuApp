package com.kashuapp.core.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.ui.graphics.vector.ImageVector

sealed class MenuTab(
    val title: String, val icon: ImageVector
) {
    data object Categories : MenuTab(
        title = "Categorías", icon = Icons.Default.Category
    )

    data object Debts : MenuTab(
        title = "Debts", icon = Icons.Default.Wallet
    )

    data object Accounts : MenuTab(

        title = "Accounts", icon = Icons.Default.Person
    )

    companion object {
        val tabs: List<MenuTab>
            get() = listOf(Categories, Debts, Accounts)
    }
}