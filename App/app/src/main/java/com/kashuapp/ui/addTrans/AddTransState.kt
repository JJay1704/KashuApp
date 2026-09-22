package com.kashuapp.ui.addTrans

import com.kashuapp.data.accounts.Account
import com.kashuapp.data.category.Category
import kotlinx.serialization.SerialName

data class AddTransState(
    val id: String? = null,
    val userId: String ?= null,
    val type: String = "",
    val description: String = "",
    val amount: String = "",
    val accountName: String = "",
    val accountId: String = "",
    val categoryName: String = "",
    val categoryId: String = "",
    val selectedAccount: Account? = null,
    val selectedCategory: Category? = null,
    val date: String = "",
    val time: String = "",
    val errorMessage : String = "",



    )
