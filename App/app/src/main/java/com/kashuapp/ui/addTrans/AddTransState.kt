package com.kashuapp.ui.addTrans

import com.kashuapp.data.accounts.Account
import com.kashuapp.data.category.Category
import kotlinx.serialization.SerialName

data class AddTransState(



    val id: String ?= null,
    val userId: String= "",

    val type: String= "",
    val description: String= "",
    val amount: String = "",
    val accountName : String = "",
    val accountId : String = "",
    val categoryName : String  = "",
    val categoryId : String  = "",
    val selectedAccount: Account? = null,
    val selectedCategory: Category? = null,

    val date : String = " ",
    val time : String =  "",


    @SerialName("transaction_date") val transactionDate: String = date + time,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null




)
