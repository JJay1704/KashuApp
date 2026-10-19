package com.kashuapp.data.budget

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Budget(
    val id: String? = null,
    @SerialName("user_id") val userId: String? = null,
    @SerialName("category_id") val categoryId: String,
    @SerialName("amount_limit") val amountLimit: Double,
    val month: Int,
    val year: Int,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null
)

data class BudgetOverview(
    val budget: Budget,
    val categoryName: String,
    val spentAmount: Double,
    val progressFraction: Float = 0.0f,
    val percentage: Int = 0,
    val remainingAmount: Double = 0.0,
    val isWarning: Boolean = false,
    val isExceeded: Boolean = false
)
