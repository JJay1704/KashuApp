package com.kashuapp.data.accounts

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class Account(


    val id: String = "",
    @SerialName("user_id") val userId: String? = null,
    val name: String,
    val type: String = "banco",                         // ej: "banco", "efectivo", "billetera_digital"
    @SerialName("initial_balance") val initialBalance: Double = 0.0,
    @SerialName("current_balance") val currentBalance: Double = 0.0,
    val currency: String = "PEN",
    val color: String = "#34D399",
    val icon: String = "account_balance",
    @SerialName("is_active") val isActive: Boolean = true,
    @SerialName("created_at") val createdAt: String = "",
    @SerialName("updated_at") val updatedAt: String = ""

)