package com.kashuapp.ui.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kashuapp.data.auth.AuthRepository
import com.kashuapp.data.auth.IAuthRepository
import com.kashuapp.data.category.CategoryRepository
import com.kashuapp.data.category.ICategoryRepository
import com.kashuapp.data.transaction.ITransactionRepository
import com.kashuapp.data.transaction.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import com.kashuapp.core.finance.FinanceRules
import com.kashuapp.data.account.AccountRepository
import com.kashuapp.data.account.IAccountRepository
import kotlinx.coroutines.Job

class AnalyticsVM(
    private val transRepo: ITransactionRepository = TransactionRepository(),
    private val catRepo: ICategoryRepository = CategoryRepository(),
    private val authRepo: IAuthRepository = AuthRepository(),
    private val accountRepo: IAccountRepository = AccountRepository()
) : ViewModel() {

    private val initialCal = Calendar.getInstance()
    private val initialMonth = initialCal.get(Calendar.MONTH) + 1
    private val initialYear = initialCal.get(Calendar.YEAR)

    private val _uiState = MutableStateFlow(
        AnalyticsState(
            selectedMonth = initialMonth,
            selectedYear = initialYear
        )
    )
    val uiState = _uiState.asStateFlow()
    private var loadJob: Job? = null
    private var loadVersion = 0

    fun selectCurrency(currency: String) {
        _uiState.update { it.copy(selectedCurrency = currency) }
        loadData()
    }

    init {
        loadData()
    }

    fun loadData() {
        val userId = authRepo.getCurrentUserId().id
        if (userId.isBlank() || userId == "Not Gotten") {
            _uiState.update { it.copy(isLoading = false, errorMessage = "Inicia sesión para ver tus estadísticas.") }
            return
        }

        loadJob?.cancel()
        val version = ++loadVersion
        loadJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val month = _uiState.value.selectedMonth
            val year = _uiState.value.selectedYear

            val calendar = Calendar.getInstance()
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month - 1)
            calendar.set(Calendar.DAY_OF_MONTH, 1)
            val maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

            val monthStr = if (month < 10) "0$month" else "$month"
            val maxDayStr = if (maxDay < 10) "0$maxDay" else "$maxDay"
            val (startDate, endDate) = FinanceRules.monthRange(year, month)
            val currency = _uiState.value.selectedCurrency
            val accountResult = accountRepo.getAllAccount(userId)
            if (accountResult.isFailure) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "No se pudieron cargar las monedas de las cuentas") }
                return@launch
            }
            val accounts = accountResult.getOrThrow()
            val accountIds = accounts.filter { it.currency == currency }.mapNotNull { it.id }.toSet()

            val catList = catRepo.getAllCateg(userId).getOrDefault(emptyList())

            val result = transRepo.getAll(
                userId = userId,
                accountId = null,
                categoryId = null,
                date = null,
                startDate = startDate,
                endDate = endDate,
                type = null
            )

            if (version != loadVersion) return@launch
            result.onSuccess { allTransactions ->
                val transactions = allTransactions.filter { it.accountId in accountIds }
                val incomes = transactions.filter { it.type == "INCOME" }
                val expenses = transactions.filter { it.type == "EXPENSE" }

                val totalIncome = incomes.sumOf { it.amount }
                val totalExpense = expenses.sumOf { it.amount }
                val netSavings = totalIncome - totalExpense
                val rawRate = if (totalIncome > 0.0) {
                    ((netSavings / totalIncome) * 100).toInt()
                } else {
                    0
                }
                val savingsRate = if (rawRate > 0) rawRate else 0

                val categoryShares = expenses.groupBy { it.categoryId }
                    .map { (catId, itemsInCat) ->
                        val sum = itemsInCat.sumOf { it.amount }
                        val cat = catList.find { it.id == catId }
                        val pct = if (totalExpense > 0.0) ((sum / totalExpense) * 100).toFloat() else 0f
                        val angle = (pct / 100f) * 360f

                        CategoryExpenseShare(
                            categoryId = catId,
                            categoryName = cat?.name ?: "Sin categoría",
                            amount = sum,
                            percentage = pct,
                            sweepAngle = angle
                        )
                    }
                    .sortedByDescending { it.amount }

                _uiState.update {
                    it.copy(
                        totalIncome = totalIncome,
                        currencies = (accounts.map { it.currency } + currency).distinct().sorted(),
                        totalExpense = totalExpense,
                        netSavings = netSavings,
                        savingsRate = savingsRate,
                        categoryExpenses = categoryShares,
                        hasData = totalIncome > 0.0 || totalExpense > 0.0,
                        isLoading = false
                    )
                }
            }

            result.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Error al calcular estadísticas"
                    )
                }
            }
        }
    }

    fun changeMonth(offset: Int) {
        val currentMonth = _uiState.value.selectedMonth
        val currentYear = _uiState.value.selectedYear

        var newMonth = currentMonth + offset
        var newYear = currentYear

        if (newMonth < 1) {
            newMonth = 12
            newYear -= 1
        } else if (newMonth > 12) {
            newMonth = 1
            newYear += 1
        }

        _uiState.update {
            it.copy(
                selectedMonth = newMonth,
                selectedYear = newYear
            )
        }
        loadData()
    }
}
