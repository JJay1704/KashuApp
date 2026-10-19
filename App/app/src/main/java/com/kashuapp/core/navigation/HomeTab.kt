package com.kashuapp.core.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Savings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class HomeTab(
    val title: String,
    val icon: ImageVector
) {
    data object Home : HomeTab(
        title = "Home",
        icon = Icons.Default.Home
    )

    data object History : HomeTab(
        title = "History",
        icon = Icons.AutoMirrored.Filled.ReceiptLong
    )

    data object Goals : HomeTab(
        title = "Goals",
        icon = Icons.Default.Savings
    )

    data object Analytics : HomeTab(
        title = "Analytics",
        icon = Icons.Default.PieChart
    )

    data object Budget : HomeTab(
        title = "Budget",
        icon = Icons.Default.AccountBalanceWallet
    )

    companion object {
        val tabs: List<HomeTab>
            get() = listOf(Home, History, Analytics, Budget, Goals)
    }
}