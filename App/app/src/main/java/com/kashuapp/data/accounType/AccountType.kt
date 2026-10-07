package com.kashuapp.data.accounType

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class AccountType(


    val id: String = "",
    val name: String,
    @SerialName("icon_name") val iconName: String = "Smartphone",
    val canceled: Boolean = false,

    )