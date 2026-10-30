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
    val currency: String = "PEN",
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null
)

