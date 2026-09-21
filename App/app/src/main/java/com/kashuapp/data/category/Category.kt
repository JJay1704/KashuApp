package com.kashuapp.data.category

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class Category(


    val id: String = "",
    @SerialName("user_id") val userId: String? = null,
    val name: String,
    val type: String = "EXPENSE",
    val icon: String = "category",
    val color: String = "#34D399",
    @SerialName("is_default") val isDefault: Boolean = false,
    @SerialName("created_at") val createdAt: String? = null

)