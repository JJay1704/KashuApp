package com.kashuapp.ui.budget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kashuapp.data.auth.AuthRepository
import com.kashuapp.data.auth.IAuthRepository
import com.kashuapp.data.budget.Budget
import com.kashuapp.data.budget.BudgetRepository
import com.kashuapp.data.budget.IBudgetRepository
import com.kashuapp.data.category.Category
import com.kashuapp.data.category.CategoryRepository
import com.kashuapp.data.category.ICategoryRepository
import com.kashuapp.data.transaction.ITransactionRepository
import com.kashuapp.data.transaction.Transaction
import com.kashuapp.data.transaction.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import com.kashuapp.core.finance.FinanceRules
import com.kashuapp.data.account.AccountRepository
import com.kashuapp.data.account.IAccountRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.Job

class BudgetVM(
    private val budgetRepo: IBudgetRepository = BudgetRepository(),
    private val catRepo: ICategoryRepository = CategoryRepository(),
    private val transRepo: ITransactionRepository = TransactionRepository(),
    private val authRepo: IAuthRepository = AuthRepository(),
    private val accountRepo: IAccountRepository = AccountRepository()
) : ViewModel() {

    private val initialCalendar = Calendar.getInstance()
    private val initialMonth = initialCalendar.get(Calendar.MONTH) + 1
    private val initialYear = initialCalendar.get(Calendar.YEAR)

    private val _uiState = MutableStateFlow(
        BudgetState(
            selectedMonth = initialMonth,
            selectedYear = initialYear
        )
    )
    val uiState = _uiState.asStateFlow()

    private var currentTransactions: List<Transaction> = emptyList()
    private var loadJob: Job? = null
    private var loadVersion = 0
    private var observedMonth = initialYear * 12 + initialMonth

    init {
        loadData()
        viewModelScope.launch {
            while (true) {
                delay(30_000)
                refreshPeriod()
            }
        }
    }

    fun refreshPeriod() {
        val now = Calendar.getInstance()
        val month = now.get(Calendar.MONTH) + 1
        val year = now.get(Calendar.YEAR)
        if (year * 12 + month != observedMonth) {
            observedMonth = year * 12 + month
            _uiState.update { it.copy(selectedMonth = month, selectedYear = year) }
            loadData()
        }
    }

    fun selectCurrency(currency: String) {
        _uiState.update { it.copy(selectedCurrency = currency) }
        loadData()
    }

    fun getCategoryName(categoryId: String): String {
        return _uiState.value.categories.find { it.id == categoryId }?.name ?: "Sin categoría"
    }

    fun getSpentForCategory(categoryId: String): Double {
        return currentTransactions
            .filter { it.categoryId == categoryId }
            .sumOf { it.amount }
    }

    fun loadData() {
        val userId = authRepo.getCurrentUserId().id
        if (userId.isBlank() || userId == "Not Gotten") {
            _uiState.update { it.copy(isLoading = false, errorMessage = "Sesión no válida") }
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

            val categoryResult = catRepo.getAllCateg(userId)
            val accountResult = accountRepo.getAllAccount(userId)
            if (categoryResult.isFailure || accountResult.isFailure) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "No se pudieron cargar categorías o cuentas") }
                return@launch
            }
            val categories = categoryResult.getOrThrow()
            val accounts = accountResult.getOrThrow()
            val accountIds = accounts.filter { it.currency == currency }.mapNotNull { it.id }.toSet()
            val transactionResult = transRepo.getAll(
                userId = userId,
                accountId = null,
                categoryId = null,
                date = null,
                startDate = startDate,
                endDate = endDate,
                type = "EXPENSE"
            )
            if (transactionResult.isFailure) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "No se pudieron cargar los gastos") }
                return@launch
            }
            val transactions = transactionResult.getOrThrow().filter { it.accountId in accountIds }

            currentTransactions = transactions

            val budgetResult = budgetRepo.getBudgets(userId, month, year)
            if (version != loadVersion) return@launch
            budgetResult.onSuccess { allBudgets ->
                val budgetList = allBudgets.filter { it.currency == currency }
                val totalBudget = budgetList.sumOf { it.amountLimit }
                val totalSpent = budgetList.sumOf { budget ->
                    transactions
                        .filter { it.categoryId == budget.categoryId }
                        .sumOf { it.amount }
                }

                val rawOverall = if (totalBudget > 0.0) {
                    (totalSpent / totalBudget).toFloat()
                } else {
                    0.0f
                }
                val overallFraction = if (rawOverall > 1.0f) 1.0f else if (rawOverall < 0.0f) 0.0f else rawOverall
                val overallPct = if (totalBudget > 0.0) {
                    ((totalSpent / totalBudget) * 100).toInt()
                } else {
                    0
                }

                _uiState.update {
                    it.copy(
                        budgets = budgetList,
                        currencies = (accounts.map { it.currency } + allBudgets.map { it.currency } + currency).distinct().sorted(),
                        categories = categories.filter { cat -> cat.type == "EXPENSE" },
                        totalBudgeted = totalBudget,
                        totalSpent = totalSpent,
                        overallProgressFraction = overallFraction,
                        overallPercentage = overallPct,
                        isLoading = false
                    )
                }
            }

            budgetResult.onFailure { error ->
                _uiState.update {
                    it.copy(
                        errorMessage = error.message ?: "Error al cargar presupuestos",
                        isLoading = false
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

    fun openCreate() {
        _uiState.update {
            it.copy(
                isFormOpen = true,
                isEditing = false,
                budgetToEdit = null,
                categoryId = "",
                categoryName = "",
                amountLimit = "",
                isAmountError = null,
                isCategoryError = null,
                errorMessage = null
            )
        }
    }

    fun openEdit(budget: Budget) {
        _uiState.update {
            it.copy(
                isFormOpen = true,
                isEditing = true,
                budgetToEdit = budget,
                categoryId = budget.categoryId,
                categoryName = getCategoryName(budget.categoryId),
                amountLimit = budget.amountLimit.toString(),
                isAmountError = null,
                isCategoryError = null,
                errorMessage = null
            )
        }
    }

    fun closeForm() {
        _uiState.update {
            it.copy(
                isFormOpen = false,
                isEditing = false,
                budgetToEdit = null,
                isAmountError = null,
                isCategoryError = null,
                errorMessage = null
            )
        }
    }

    fun onCategory(category: Category) {
        _uiState.update {
            it.copy(
                categoryId = category.id ?: "",
                categoryName = category.name,
                isCategoryError = null
            )
        }
    }

    fun onAmountLimit(amount: String) {
        _uiState.update { it.copy(amountLimit = amount, isAmountError = null) }
    }

    fun saveBudget() {
        val state = _uiState.value
        val amount = FinanceRules.positiveAmount(state.amountLimit)
        val amountErr = if (amount == null || amount <= 0.0) "Ingrese monto válido mayor a 0" else null
        val categoryErr = if (state.categoryId.isBlank()) "Seleccione una categoría válida" else null

        if (amountErr != null || categoryErr != null) {
            _uiState.update {
                it.copy(
                    isAmountError = amountErr,
                    isCategoryError = categoryErr
                )
            }
            return
        }

        val validAmount = amount ?: return
        val userId = authRepo.getCurrentUserId().id

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val budget = Budget(
                id = state.budgetToEdit?.id,
                userId = userId,
                categoryId = state.categoryId,
                amountLimit = validAmount,
                month = state.selectedMonth,
                year = state.selectedYear
                , currency = state.selectedCurrency
            )

            val result = if (state.budgetToEdit != null) {
                budgetRepo.updateBudget(budget)
            } else {
                budgetRepo.createBudget(budget)
            }

            _uiState.update { it.copy(isLoading = false) }

            result.onSuccess {
                closeForm()
                loadData()
            }
            result.onFailure { error ->
                _uiState.update {
                    it.copy(
                        errorMessage = error.message ?: "Error al guardar presupuesto"
                    )
                }
            }
        }
    }

    fun deleteBudget(budgetId: String?) {
        if (budgetId == null) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = budgetRepo.deleteBudget(budgetId)
            result.onSuccess {
                closeForm()
                loadData()
            }
            result.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Error al eliminar"
                    )
                }
            }
        }
    }
}
