package com.kashuapp.data.debt

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Debt(
    val id: String,
    @SerialName("user_id") val userId: String,
    val name: String,
    val creditor: String? = null,
    val currency: String,
    @SerialName("initial_amount") val initialAmount: Double,
    @SerialName("outstanding_amount") val outstandingAmount: Double
)

@Serializable
data class DebtPayment(
    val id: String,
    @SerialName("debt_id") val debtId: String,
    @SerialName("account_id") val accountId: String,
    val amount: Double,
    val currency: String,
    @SerialName("paid_at") val paidAt: String,
    val description: String = "",
    @SerialName("user_id") val userId: String = ""
)
