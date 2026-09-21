package com.kashuapp.data.transaction

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Transaction(

    val id: String ? = null,
    @SerialName("user_id") val userId: String? = null,
    @SerialName("account_id") val accountId: String,
    @SerialName("category_id") val categoryId: String,
    val amount: Double,
    val type: String,
    val description: String? = null,
    @SerialName("transaction_date") val transactionDate: String = "",
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null

)