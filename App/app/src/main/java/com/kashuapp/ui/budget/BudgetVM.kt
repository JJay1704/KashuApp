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

class BudgetVM(
    private val budgetRepo: IBudgetRepository = BudgetRepository(),
    private val catRepo: ICategoryRepository = CategoryRepository(),
    private val transRepo: ITransactionRepository = TransactionRepository(),
    private val authRepo: IAuthRepository = AuthRepository()
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

    init {
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

        viewModelScope.launch {
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
            val startDate = "$year-$monthStr-01 00:00"
            val endDate = "$year-$monthStr-$maxDayStr 23:59"

            val categories = catRepo.getAllCateg(userId).getOrDefault(emptyList())
            val transactions = transRepo.getAll(
                userId = userId,
                accountId = null,
                categoryId = null,
                date = null,
                startDate = startDate,
                endDate = endDate,
                type = "EXPENSE"
            ).getOrDefault(emptyList())

            currentTransactions = transactions

            val budgetResult = budgetRepo.getBudgets(userId, month, year)
            budgetResult.onSuccess { budgetList ->
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
                val overallFraction =
                    if (rawOverall > 1.0f) 1.0f else if (rawOverall < 0.0f) 0.0f else rawOverall
                val overallPct = if (totalBudget > 0.0) {
                    ((totalSpent / totalBudget) * 100).toInt()
                } else {
                    0
                }

                _uiState.update {
                    it.copy(
                        budgets = budgetList,
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


    fun isRepeated(categoryId: String?, currentBudgetId: String?): Boolean {

        var categoryRepeated = uiState.value.budgets.any { it.categoryId == categoryId && it.id != currentBudgetId  }



        if (categoryRepeated ){

            return true
        } else return false

    }

    fun saveBudget() {
        val state = _uiState.value
        val amount = state.amountLimit.toDoubleOrNull()
        val amountErr =
            if (amount == null || amount <= 0.0) "Ingrese monto válido mayor a 0" else null
        val categoryErr : String? = when{

            state.categoryId.isBlank() -> {"Seleccione una categoría válida"}


            isRepeated(state.categoryId ,state.budgetToEdit?.id) -> {"Presupuesto ya definido para esta categoria "}
            else ->null

        }









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
