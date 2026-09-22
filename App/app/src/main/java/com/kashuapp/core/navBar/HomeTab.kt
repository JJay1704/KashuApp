package com.kashuapp.core.navBar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.SwipeUpAlt
import androidx.compose.ui.graphics.vector.ImageVector

sealed class HomeTab(
    val title: String,
    val icon: ImageVector
) {
    data object Home : HomeTab(
        title = "Home",
        icon = Icons.Default.Home
    )

    data object Goals : HomeTab(
        title = "Goals",
        icon = Icons.Default.Savings
    )

    data object Transaction : HomeTab(
        title = "Transaction",
        icon = Icons.AutoMirrored.Filled.ReceiptLong
    )


    companion object {
        val tabs: List<HomeTab>
            get() = listOf(Home, Goals, Transaction)
    }
}