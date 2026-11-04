package com.kashuapp.ui.home

data class AccountByCurrency(
    val currency: String,
    val totalBalance: Double = 0.0,
    val totalDebt: Double = 0.0,
    val netWorth: Double = 0.0,
    val formattedBalance: String = "",
    val formattedDebt: String = "",
    val formattedNetWorth: String = ""
)