package com.kashuapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kashuapp.data.account.AccountRepository
import com.kashuapp.data.account.IAccountRepository
import com.kashuapp.data.auth.AuthRepository
import com.kashuapp.data.auth.IAuthRepository
import com.kashuapp.data.category.CategoryRepository
import com.kashuapp.data.category.ICategoryRepository
import com.kashuapp.data.debt.DebtRepository
import com.kashuapp.data.debt.IDebtRepository
import com.kashuapp.data.transaction.ITransactionRepository
import com.kashuapp.data.transaction.Transaction
import com.kashuapp.data.transaction.TransactionRepository
import com.kashuapp.ui.transaction.list.historyTimestamp
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeVM(
    private val authRepo: IAuthRepository = AuthRepository(),
    private val accountRepo: IAccountRepository = AccountRepository(),
    private val transRepo: ITransactionRepository = TransactionRepository(),
    private val catRepo: ICategoryRepository = CategoryRepository(),
    private val debtRepo: IDebtRepository = DebtRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeState())
    val uiState: StateFlow<HomeState> = _uiState.asStateFlow()
    init {
        loadData()
    }

    fun loadData() {
        if (_uiState.value.isLoading) return

        val userId = authRepo.getCurrentUserId().id

        if (userId.isBlank() || userId == "Not Gotten") {
            _uiState.update {
                it.copy(errorMessage = "Sesión no válida")
            }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true, errorMessage = null)
            }

            try {
                val transactions = transRepo.getAll(userId).getOrThrow()
                val categories = catRepo.getAllCateg(userId).getOrThrow()
                val accounts = accountRepo.getAllAccount(userId).getOrThrow()
                val debts = debtRepo.getDebts(userId).getOrThrow()
                val payments = debtRepo.getPayments(userId).getOrDefault(emptyList())

                val debtCategory = categories.find {
                    it.name.contains("deuda", ignoreCase = true)
                }
                val debtNames = debts.associate { it.id to it.name }
                val debtTransactions = payments.map { payment ->
                    val debtName = debtNames[payment.debtId] ?: "Deuda"
                    val desc = if (payment.description.isNotBlank()) {
                        "$debtName - ${payment.description}"
                    } else {
                        "Abono a deuda: $debtName"
                    }
                    Transaction(
                        id = payment.id,
                        userId = payment.userId,
                        accountId = payment.accountId,
                        categoryId = debtCategory?.id ?: "",
                        amount = payment.amount,
                        type = "EXPENSE",
                        description = desc,
                        transactionDate = payment.paidAt
                    )
                }

                val balances = accounts
                    .filter { it.isActive }
                    .groupBy { it.currency }
                    .map { (currency, accountsInCurrency) ->
                        val totalBalance = accountsInCurrency.sumOf {
                            it.currentBalance
                        }
                        val totalDebt = debts
                            .filter { it.currency == currency }
                            .sumOf { it.outstandingAmount }

                        val netWorth = totalBalance - totalDebt

                        AccountByCurrency(
                            currency = currency,
                            totalBalance = totalBalance,
                            totalDebt = totalDebt,
                            netWorth = netWorth,
                            formattedBalance = "$currency $totalBalance",
                            formattedDebt = "- $currency $totalDebt",
                            formattedNetWorth = "$currency $netWorth"
                        )
                    }

                _uiState.update {
                    it.copy(
                        accounts = accounts,
                        accountsByCurrency = balances,
                        transactions = (transactions + debtTransactions)
                            .sortedByDescending { transaction ->
                                runCatching { historyTimestamp(transaction.transactionDate) }
                                    .getOrDefault(0L)
                            }
                            .take(5),
                        categories = categories,
                        isLoading = false,
                        errorMessage = null
                    )
                }
            } catch (error: CancellationException) {
                    throw error
            } catch (error: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "No se pudo cargar el inicio"
                    )
                }
            }
        }
    }

    fun getCategoryName(categoryId: String): String {
        return _uiState.value.categories
            .find { it.id == categoryId }
            ?.name ?: "Sin categoría"
    }

    fun getAccountName(accountId: String?): String {
        return _uiState.value.accounts
            .find { it.id == accountId }
            ?.name ?: "Sin cuenta"
    }
}
