package com.kashuapp.ui.transaction.list

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kashuapp.core.finance.FinanceRules
import com.kashuapp.data.account.*
import com.kashuapp.data.auth.*
import com.kashuapp.data.category.*
import com.kashuapp.data.transaction.*
import kotlinx.coroutines.Job
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
    var transactions by mutableStateOf<List<Transaction>>(emptyList()); private set
    var categories by mutableStateOf<List<Category>>(emptyList()); private set
    var accounts by mutableStateOf<List<Account>>(emptyList()); private set
    var errorMessage by mutableStateOf<String?>(null); private set
    var isLoading by mutableStateOf(false); private set
    val periods = listOf("Today", "This week", "This Month", "Last Month", "Personalizado")
    var selectedPeriod by mutableStateOf<String?>(null); private set
    var startDate by mutableStateOf(""); private set
    var endDate by mutableStateOf(""); private set
    var selectedAccount by mutableStateOf<Account?>(null); private set
    var selectedCategory by mutableStateOf<Category?>(null); private set
    val transactionTypes = listOf("EXPENSE", "INCOME")
    var selectedType by mutableStateOf<String?>(null); private set
    private var queryJob: Job? = null
    private var requestVersion = 0

    init { loadData() }

    fun loadData() {
        viewModelScope.launch {
            val userId = authRepo.getCurrentUserId().id
            catRepo.getAllCateg(userId).onSuccess { categories = it }
                .onFailure { errorMessage = it.message }
            getTrans()
        }
    }

    fun selectedType(type: String?) { selectedType = type; getTrans() }
    fun selectAccount(account: Account?) { selectedAccount = account; getTrans() }
    fun selectCategory(category: Category?) { selectedCategory = category; getTrans() }
    fun selectPeriod(period: String?) {
        selectedPeriod = period
        if (period == "Personalizado" && startDate.isBlank()) {
            startDate = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Calendar.getInstance().time)
            endDate = startDate
        }
        getTrans()
    }
    fun selectStartDate(value: String) { startDate = value; getTrans() }
    fun selectEndDate(value: String) { endDate = value; getTrans() }

    private fun range(): Pair<String?, String?>? {
        val calendar = Calendar.getInstance()
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val today = format.format(calendar.time)
        return when (selectedPeriod) {
            null -> null to null
            "Personalizado" -> FinanceRules.dateRange(startDate, endDate)
            "Today" -> FinanceRules.dateRange(today, today)
            "This week" -> {
                calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
                val first = format.format(calendar.time)
                calendar.add(Calendar.DAY_OF_MONTH, 6)
                FinanceRules.dateRange(first, format.format(calendar.time))
            }
            "Last Month" -> {
                calendar.add(Calendar.MONTH, -1)
                FinanceRules.monthRange(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1)
            }
            else -> FinanceRules.monthRange(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1)
        }
    }

    fun getTrans() {
        queryJob?.cancel()
        val version = ++requestVersion
        val dates = range()
        if (dates == null) { isLoading = false; errorMessage = "Elige un rango válido: Desde no puede ser posterior a Hasta"; return }
        errorMessage = null
        val accountId = selectedAccount?.id
        val categoryId = selectedCategory?.id
        val type = selectedType
        queryJob = viewModelScope.launch {
            isLoading = true
            val userId = authRepo.getCurrentUserId().id
            val result = transRepo.getAll(userId, accountId, categoryId, null, dates.first, dates.second, type)
            if (version != requestVersion) return@launch
            result.onSuccess { transactions = it.sortedByDescending { row -> FinanceRules.timestampMillis(row.transactionDate) } }
                .onFailure { errorMessage = it.message ?: "No se pudieron cargar los movimientos" }
            accountRepo.getAllAccount(userId).onSuccess { if (version == requestVersion) accounts = it }
                .onFailure { if (version == requestVersion) errorMessage = it.message }
            if (version == requestVersion) isLoading = false
        }
    }

    fun getCategoryName(id: String) = categories.find { it.id == id }?.name ?: "Sin categoría"
    fun getAccountName(id: String?) = accounts.find { it.id == id }?.name ?: "Sin cuenta"
    fun getBalance(id: String?) = accounts.find { it.id == id }?.let { "${it.currentBalance} ${it.currency}" } ?: "0.00"
}
