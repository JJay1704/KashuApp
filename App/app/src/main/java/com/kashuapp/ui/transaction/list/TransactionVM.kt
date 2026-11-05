package com.kashuapp.ui.transaction.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kashuapp.data.account.Account
import com.kashuapp.data.account.AccountRepository
import com.kashuapp.data.account.IAccountRepository
import com.kashuapp.data.auth.AuthRepository
import com.kashuapp.data.auth.IAuthRepository
import com.kashuapp.data.category.Category
import com.kashuapp.data.category.CategoryRepository
import com.kashuapp.data.category.ICategoryRepository
import com.kashuapp.data.debt.DebtRepository
import com.kashuapp.data.debt.IDebtRepository
import com.kashuapp.data.transaction.ITransactionRepository
import com.kashuapp.data.transaction.Transaction
import com.kashuapp.data.transaction.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class TransactionVM(
    private val transRepo: ITransactionRepository = TransactionRepository(),
    private val catRepo: ICategoryRepository = CategoryRepository(),
    private val authRepo: IAuthRepository = AuthRepository(),
    private val accountRepo: IAccountRepository = AccountRepository(),
    private val debtRepo: IDebtRepository = DebtRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(TransactionState())
    val uiState: StateFlow<TransactionState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    val periods = listOf("Today", "This week", "This Month", "Last Month")
    val transactionTypes = listOf("EXPENSE", "INCOME", "DEBT_PAYMENT")

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
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        when (option) {
            periods[1] -> calendar.add(Calendar.DAY_OF_YEAR, -7)
            periods[2] -> calendar.set(Calendar.DAY_OF_MONTH, 1)
            periods[3] -> {
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                calendar.add(Calendar.MONTH, -1)
            }
        }
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.US)
        val start = format.format(calendar.time)
        val end = when (option) {
            periods[1], periods[2] -> null
            else -> {
                calendar.add(if (option == periods[3]) Calendar.MONTH else Calendar.DAY_OF_MONTH, 1)
                calendar.add(Calendar.MILLISECOND, -1)
                format.format(calendar.time)
            }
        }
        return start to end
    }

    fun loadData() {
        getTrans()
    }

    fun getCategoryName(categoryId: String): String {
        return _uiState.value.categories.find { it.id == categoryId }?.name ?: "Sin categoría"
    }

    fun getAccountName(accountId: String?): String {
        return _uiState.value.accounts.find { it.id == accountId }?.name ?: "Sin cuenta"
    }

    fun getBalance(accountId: String?): String {
        val account = _uiState.value.accounts.find { it.id == accountId }

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
                    isLoading = false, errorMessage = "Sesión no válida"
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

                val transactions = if (type == "DEBT_PAYMENT") emptyList() else transRepo.getAll(
                    userId = userId,
                    accountId = filterAccount,
                    categoryId = filterCategory,
                    startDate = dateRange.first,
                    endDate = dateRange.second,
                    type = type
                ).getOrThrow()

                val categories = catRepo.getAllCateg(userId).getOrThrow()
                val accounts = accountRepo.getAllAccount(userId).getOrThrow()

                val selectedCategoryObj = categories.find { it.id == filterCategory }
                val isDebtCategoryFilter =
                    selectedCategoryObj?.name?.contains("deuda", ignoreCase = true) == true
                val includePayments =
                    (filterCategory == null || isDebtCategoryFilter) && (type == null || type == "DEBT_PAYMENT")
                val payments =
                    if (includePayments) debtRepo.getPayments(userId).getOrThrow() else emptyList()
                val debtNames = if (payments.isNotEmpty()) {
                    debtRepo.getDebts(userId).getOrThrow().associate { it.id to it.name }
                } else emptyMap()

                val debtCategory = categories.find {
                    it.name.contains("deuda", ignoreCase = true)
                }
                val startMillis = dateRange.first?.let { parseTransactionDate(it) }
                val endMillis = dateRange.second?.let { parseTransactionDate(it) }

                val debtTransactions =
                    payments.filter { filterAccount == null || it.accountId == filterAccount }
                        .filter { payment ->
                            val paidAtMillis = parseTransactionDate(payment.paidAt)
                            (startMillis == null || paidAtMillis >= startMillis) && (endMillis == null || paidAtMillis <= endMillis)
                        }.map { payment ->
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

                val allTransactions =
                    (transactions + debtTransactions).sortedByDescending { parseTransactionDate(it.transactionDate) }

                _uiState.update {
                    it.copy(
                        transactions = allTransactions,
                        categories = categories,
                        accounts = accounts,
                        isLoading = false,
                        errorMessage = null
                    )
                }
            } catch (error: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false, errorMessage = "No se pudo cargar el historial"
                    )
                }
            }
        }
    }
}

internal fun parseTransactionDate(value: String): Long {
    val text = value.trim().replace(' ', 'T').substringBefore('.')
    val patterns = listOf(
        "yyyy-MM-dd'T'HH:mm:ssXXX", "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd'T'HH:mm"
    )
    for (pattern in patterns) {
        try {
            val formatter = SimpleDateFormat(pattern, Locale.US)
            formatter.timeZone = TimeZone.getTimeZone("UTC")
            val parsed = formatter.parse(text)
            if (parsed != null) return parsed.time
        } catch (_: Exception) {
        }
    }
    return 0L
}
