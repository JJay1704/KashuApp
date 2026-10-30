package com.kashuapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kashuapp.data.account.AccountRepository
import com.kashuapp.data.account.IAccountRepository
import com.kashuapp.data.auth.AuthRepository
import com.kashuapp.data.auth.IAuthRepository
import com.kashuapp.data.category.CategoryRepository
import com.kashuapp.data.category.ICategoryRepository
import com.kashuapp.data.transaction.ITransactionRepository
import com.kashuapp.data.transaction.TransactionRepository
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
    private val catRepo: ICategoryRepository = CategoryRepository()
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

                val balances = accounts
                    .filter { it.isActive }
                    .groupBy { it.currency }
                    .map { (currency, accountsInCurrency) ->
                        val total = accountsInCurrency.sumOf {
                            it.currentBalance
                        }

                        AccountByCurrency(
                            currency = currency,
                            totalAmount = total,
                            formattedTotal = "$currency $total"
                        )
                    }

                _uiState.update {
                    it.copy(
                        accounts = accounts,
                        accountsByCurrency = balances,
                        transactions = transactions
                            .sortedByDescending { transaction ->
                                transaction.transactionDate
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