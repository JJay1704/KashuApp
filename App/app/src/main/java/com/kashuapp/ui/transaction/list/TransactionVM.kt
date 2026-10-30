package com.kashuapp.ui.transaction.list

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.setValue
import com.kashuapp.data.accounType.AccountType
import com.kashuapp.data.account.Account
import com.kashuapp.data.account.AccountRepository
import com.kashuapp.data.account.IAccountRepository
import com.kashuapp.data.auth.AuthRepository
import com.kashuapp.data.auth.IAuthRepository
import com.kashuapp.data.category.Category
import com.kashuapp.data.category.CategoryRepository
import com.kashuapp.data.category.ICategoryRepository

import com.kashuapp.data.transaction.ITransactionRepository
import com.kashuapp.data.transaction.Transaction
import com.kashuapp.data.transaction.TransactionRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class TransactionVM(
    private val transRepo: ITransactionRepository = TransactionRepository(),
    private val catRepo: ICategoryRepository = CategoryRepository(),
    private val authRepo: IAuthRepository = AuthRepository(),
    private val accountRepo: IAccountRepository = AccountRepository()

) : ViewModel() {

    private val _uiState = MutableStateFlow(TransactionState())
    val uiState: StateFlow<TransactionState> = _uiState.asStateFlow()


    init {
        loadData()
    }


    val periods = listOf("Today", "This week", "This Month", "Last Month")
    val transactionTypes = listOf("EXPENSE", "INCOME")

    fun selectAccount(account: Account?) {
        _uiState.update {
            it.copy(selectedAccount = account)
        }
        getTrans()
    }

    fun selectCategory(category: Category?) {
        _uiState.update {
            it.copy(selectedCategory = category)
        }
        getTrans()
    }

    fun selectPeriod(period: String?) {
        _uiState.update {
            it.copy(selectedPeriod = period)
        }
        getTrans()
    }

    fun selectedType(type: String?) {
        _uiState.update {
            it.copy(selectedType = type)
        }
        getTrans()
    }

    fun getDateRange(option: String): Pair<String, String?> {

        val calendar = Calendar.getInstance()
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val todayStr = sdf.format(calendar.time)
        return when (option) {
            periods[0] -> {
                Pair("$todayStr 00:00", "$todayStr 23:59")
            }

            periods[1] -> {
                calendar.add(Calendar.DAY_OF_YEAR, -7)
                val weekStart = sdf.format(calendar.time)
                Pair("$weekStart 00:00", null)
            }

            periods[2] -> {
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                val monthStart = sdf.format(calendar.time)
                Pair("$monthStart 00:00", null)
            }

            periods[3] -> {
                calendar.add(Calendar.MONTH, -1)
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                val lastMonthStart = sdf.format(calendar.time)
                val maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
                calendar.set(Calendar.DAY_OF_MONTH, maxDay)
                val lastMonthEnd = sdf.format(calendar.time)
                Pair("$lastMonthStart 00:00", "$lastMonthEnd 23:59")
            }

            else -> Pair("$todayStr 00:00", "$todayStr 23:59")
        }
    }


    fun loadData() {
        getTrans()
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

    fun getBalance(accountId: String?): String {
        val account = _uiState.value.accounts
            .find { it.id == accountId }

        return if (account != null) {
            "${account.currentBalance} ${account.currency}"
        } else {
            "0.00"
        }
    }
    fun getTrans(
        filterAccount: String? = _uiState.value.selectedAccount?.id,
        filterCategory: String? = _uiState.value.selectedCategory?.id,
        period: String? = _uiState.value.selectedPeriod,
        type: String? = _uiState.value.selectedType
    ) {
        val userId = authRepo.getCurrentUserId().id

        if (userId.isBlank() || userId == "Not Gotten") {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    errorMessage = "Sesión no válida"
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true, errorMessage = null)
            }

            try {
                val dateRange = if (period != null) {
                    getDateRange(period)
                } else {
                    Pair(null, null)
                }

                val transactions = transRepo.getAll(
                    userId = userId,
                    accountId = filterAccount,
                    categoryId = filterCategory,
                    startDate = dateRange.first,
                    endDate = dateRange.second,
                    type = type
                ).getOrThrow()

                val categories = catRepo
                    .getAllCateg(userId)
                    .getOrThrow()

                val accounts = accountRepo
                    .getAllAccount(userId)
                    .getOrThrow()

                _uiState.update {
                    it.copy(
                        transactions = transactions.sortedByDescending {
                                transaction -> transaction.transactionDate
                        },
                        categories = categories,
                        accounts = accounts,
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
                        errorMessage = "No se pudo cargar el historial"
                    )
                }
            }
        }
    }
}