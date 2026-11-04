package com.kashuapp.ui.debt

import com.kashuapp.data.account.Account
import com.kashuapp.data.debt.Debt
import com.kashuapp.data.debt.DebtPayment

data class DebtFormState(
    val id: String,
    val isEditing: Boolean = false,
    val name: String = "",
    val creditor: String = "",
    val currency: String = "PEN",
    val amount: String = ""
)

data class DebtPaymentState(
    val requestId: String,
    val debt: Debt,
    val accountId: String = "",
    val amount: String = "",
    val date: String,
    val time: String,
    val description: String = ""
)

data class DebtState(
    val debts: List<Debt> = emptyList(),
    val payments: List<DebtPayment> = emptyList(),
    val accounts: List<Account> = emptyList(),
    val paymentAccounts: List<Account> = emptyList(),
    val currencies: List<String> = listOf("PEN", "USD"),
    val totalsByCurrency: Map<String, Double> = emptyMap(),
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val infoMessage: String? = null,
    val form: DebtFormState? = null,
    val paymentForm: DebtPaymentState? = null,
    val debtToDelete: Debt? = null
)
