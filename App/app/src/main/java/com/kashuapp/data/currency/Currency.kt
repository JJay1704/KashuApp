package com.kashuapp.data.currency

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class Currency(


    val code: String,
    val name: String,
    val symbol: String,
    )