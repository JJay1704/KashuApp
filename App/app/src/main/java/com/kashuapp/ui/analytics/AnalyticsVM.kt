package com.kashuapp.ui.analytics

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
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar

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
            selectedMonth = initialMonth, selectedYear = initialYear
        )
    )
    val uiState = _uiState.asStateFlow()

     private var latestRequestId = 0L

    init {
        loadData()
    }

     fun loadData() {
         latestRequestId += 1
         val requestId = latestRequestId
         val userId = authRepo.getCurrentUserId().id
         val filters = _uiState.value
         _uiState.update {
             it.copy(isLoading = true, errorMessage = null, totalIncome = 0.0,
                 totalExpense = 0.0, netSavings = 0.0, savingsRate = 0,
                 categoryExpenses = emptyList(), hasData = false)
         }
         if (userId.isBlank() || userId == "Not Gotten") {
             _uiState.update { it.copy(isLoading = false,
                 errorMessage = "Inicia sesión para ver tus estadísticas.") }
             return
         }
         viewModelScope.launch {
             try {
                 val month = filters.selectedMonth
                 val year = filters.selectedYear
                 val calendar = Calendar.getInstance()
                 calendar.set(Calendar.DAY_OF_MONTH, 1)
                 calendar.set(Calendar.YEAR, year)
                 calendar.set(Calendar.MONTH, month - 1)
                 val maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
                 val monthStr = month.toString().padStart(2, '0')
                 val maxDayStr = maxDay.toString().padStart(2, '0')
                 val startDate = "$year-$monthStr-01 00:00"
                 val endDate = "$year-$monthStr-$maxDayStr 23:59"
                 val accounts = accountRepo.getAllAccount(userId).getOrThrow()
                     .filter { it.userId == userId }
                 val currencies = accounts.map { it.currency }.distinct().sorted()
                 val currency = if (filters.selectedCurrency in currencies) {
                     filters.selectedCurrency
                 } else {
                     currencies.firstOrNull().orEmpty()
                 }
                 val categories = catRepo.getAllCateg(userId).getOrThrow()
                 val transactions = transRepo.getAll(
                     userId = userId, accountId = null, categoryId = null,
                     startDate = startDate, endDate = endDate, type = null
                 ).getOrThrow()
                 ensureActive()
                 val knownAccountIds = accounts.mapNotNull { it.id }.toSet()
                 if (transactions.any { it.accountId !in knownAccountIds }) {
                     throw IllegalStateException("Hay movimientos cuya cuenta no se pudo identificar")
                 }
                 val accountIds = accounts.filter { it.currency == currency }
                     .mapNotNull { it.id }.toSet()
                 val filtered = transactions.filter { it.accountId in accountIds }
                 val incomes = filtered.filter { it.type == "INCOME" }
                 val expenses = filtered.filter { it.type == "EXPENSE" }
                 val totalIncome = incomes.sumOf { it.amount }
                 val totalExpense = expenses.sumOf { it.amount }
                 val netSavings = totalIncome - totalExpense
                 val savingsRate = if (totalIncome > 0.0) {
                     ((netSavings / totalIncome) * 100).toInt().coerceAtLeast(0)
                 } else 0
                 val shares = expenses.groupBy { it.categoryId }.map { (id, movements) ->
                     val amount = movements.sumOf { it.amount }
                     val percentage = if (totalExpense > 0.0) {
                         ((amount / totalExpense) * 100).toFloat()
                     } else 0f
                     CategoryExpenseShare(
                         categoryId = id,
                         categoryName = categories.find { it.id == id }?.name ?: "Sin categoría",
                         amount = amount, percentage = percentage,
                         sweepAngle = percentage / 100f * 360f
                     )
                 }.sortedByDescending { it.amount }
                 if (requestId == latestRequestId) {
                     _uiState.update {
                         it.copy(selectedCurrency = currency, availableCurrencies = currencies,
                             totalIncome = totalIncome, totalExpense = totalExpense,
                             netSavings = netSavings, savingsRate = savingsRate,
                             categoryExpenses = shares, hasData = filtered.isNotEmpty(),
                             isLoading = false, errorMessage = null)
                     }
                 }
             } catch (error: CancellationException) {
                 throw error
             } catch (error: Exception) {
                 ensureActive()
                 if (requestId == latestRequestId) {
                     _uiState.update { it.copy(isLoading = false,
                         errorMessage = error.message ?: "No se pudieron calcular las estadísticas") }
                 }
             }
         }
     }
     fun changeCurrency(currency: String) {
         val state = _uiState.value
         if (currency != state.selectedCurrency && currency in state.availableCurrencies) {
             _uiState.update { it.copy(selectedCurrency = currency) }
             loadData()
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
                selectedMonth = newMonth, selectedYear = newYear
            )
        }
        loadData()
    }
}
