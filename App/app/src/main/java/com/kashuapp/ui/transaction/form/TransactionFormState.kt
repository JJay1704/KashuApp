package com.kashuapp.ui.transaction.form

import com.kashuapp.data.accounts.Account
import com.kashuapp.data.category.Category

data class TransactionFormState(
    val id: String? = null,
    val userId: String? = null,
    val selectedTypeTrans: String = "EXPENSE",
    val amount: String = "",
    val isAmountError: String? = null,
    val accountId: String = "",
    val accountName: String = "",
    val isAccountError: String? = null,
    val categoryId: String = "",
    val categoryName: String = "",
    val isCategoryError: String? = null,
    val date: String = "",
    val time: String = "",
    val description: String = "",
    val categories: List<Category> = emptyList(),
    val accounts: List<Account> = emptyList(),
    val selectedAccount: Account? = null,
    val selectedCategory: Category? = null,
    val errorMessage: String = "",


    )