package com.kashuapp.ui.home

data class AccountByCurrency (
    val currency: String,
    val totalAmount: Double,
    val formattedTotal: String,
)