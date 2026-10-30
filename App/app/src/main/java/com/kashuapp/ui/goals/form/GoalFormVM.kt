package com.kashuapp.ui.goals.form

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
import com.kashuapp.data.goal.Goal
import com.kashuapp.data.goal.GoalRepository
import com.kashuapp.data.goal.IGoalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.kashuapp.core.finance.FinanceRules
import com.kashuapp.data.currency.CurrencyRepository
import com.kashuapp.data.currency.ICurrencyRepository
import com.kashuapp.data.accounType.AccountTypeRepository
import com.kashuapp.data.accounType.IAccountTypeRepository

class GoalFormVM(
    private val goalRepo: IGoalRepository = GoalRepository(),
    private val authRepo: IAuthRepository = AuthRepository(),
    private val accountRepo: IAccountRepository = AccountRepository(),
    private val catRepo: ICategoryRepository = CategoryRepository(),
    private val currencyRepo: ICurrencyRepository = CurrencyRepository(),
    private val typeRepo: IAccountTypeRepository = AccountTypeRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(GoalFormState())
    val uiState = _uiState.asStateFlow()

    fun initForm(goalToEdit: Goal?) {
        if (goalToEdit != null) {
            _uiState.value = GoalFormState(
                title = goalToEdit.title,
                targetAmount = goalToEdit.targetAmount.toString(),
                initialAmount = goalToEdit.currentAmount.toString(),
                deadline = goalToEdit.deadline,
                currency = goalToEdit.currency,
                accountId = goalToEdit.accountId,
                categoryId = goalToEdit.categoryId,
                isEditing = true
            )
        } else {
            resetForm()
        }

        viewModelScope.launch {
            val userId = authRepo.getCurrentUserId().id
            val accList = accountRepo.getAllAccount(userId).getOrDefault(emptyList())
            val catList = catRepo.getAllCateg(userId).getOrDefault(emptyList())
            val currencyList = currencyRepo.getAllCurrency().getOrDefault(emptyList())
            val types = typeRepo.getAllAccountTypes().getOrDefault(emptyList())

            val matchedAccount = accList.find { it.id == _uiState.value.accountId }
            val matchedCategory = catList.find { it.id == _uiState.value.categoryId }

            _uiState.update { current ->
                current.copy(
                    accounts = accList.filter { it.isActive && !FinanceRules.isLiability(it, types) },
                    currencies = (currencyList.map { it.code } + current.currency).distinct(),
                    categories = catList,
                    accountName = matchedAccount?.name ?: current.accountName,
                    categoryName = matchedCategory?.name ?: current.categoryName,
                    currency = goalToEdit?.currency ?: matchedAccount?.currency ?: current.currency
                )
            }
        }
    }

    fun resetForm() {
        _uiState.value = GoalFormState(
            accounts = _uiState.value.accounts,
            categories = _uiState.value.categories,
            isEditing = false
        )
    }

    fun onTitle(newTitle: String) {
        _uiState.update { it.copy(title = newTitle, isTitleError = null) }
    }

    fun onTargetAmount(newAmount: String) {
        _uiState.update { it.copy(targetAmount = newAmount, isTargetAmountError = null) }
    }

    fun onInitialAmount(newAmount: String) {
        _uiState.update { it.copy(initialAmount = newAmount) }
    }

    fun onCurrency(currency: String) {
        if (_uiState.value.isEditing) return
        _uiState.update { it.copy(currency = currency, accountId = null, accountName = "") }
    }

    fun onDeadline(newDeadline: String) {
        _uiState.update { it.copy(deadline = newDeadline, isDeadlineError = null) }
    }

    fun onAccount(newAccount: Account) {
        _uiState.update {
            it.copy(
                accountId = newAccount.id,
                accountName = newAccount.name,
                currency = newAccount.currency
            )
        }
    }

    fun onCategory(newCategory: Category) {
        _uiState.update {
            it.copy(
                categoryId = newCategory.id,
                categoryName = newCategory.name
            )
        }
    }

    fun deleteGoal(goalId: String?, onSuccess: () -> Unit) {
        if (goalId == null) return

        viewModelScope.launch {
            val result = goalRepo.deleteGoal(goalId)
            result.onSuccess {
                onSuccess()
                resetForm()
            }
            result.onFailure { error ->
                _uiState.update { it.copy(errorMessage = error.message ?: "No se pudo eliminar la meta") }
            }
        }
    }

    fun saveGoal(goalToEdit: Goal?, onSuccess: () -> Unit) {
        val state = _uiState.value
        if (state.isLoading) return
        val parsedTarget = FinanceRules.positiveAmount(state.targetAmount)
        val parsedInitial = if (goalToEdit != null) goalToEdit.currentAmount
            else if (state.initialAmount.isBlank()) 0.0 else state.initialAmount.toDoubleOrNull()
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        val titleErr = if (state.title.trim().isBlank()) "El nombre es obligatorio" else null
        val targetErr = if (parsedTarget == null || parsedTarget <= 0.0) "Ingrese monto válido mayor a 0" else null
        val deadlineErr = when {
            state.deadline.trim().isBlank() -> "La fecha límite es obligatoria"
            FinanceRules.dateRange(state.deadline, state.deadline) == null -> "La fecha límite no es válida"
            goalToEdit == null && state.deadline < today -> "La fecha límite debe ser hoy o futura"
            else -> null
        }

        if (parsedInitial == null || !parsedInitial.isFinite() || parsedInitial < 0.0) {
            _uiState.update { it.copy(errorMessage = "El ahorro inicial debe ser un monto válido no negativo") }
            return
        }
        if (goalToEdit != null && state.currency != goalToEdit.currency) {
            _uiState.update { it.copy(errorMessage = "La moneda de una meta existente no se puede cambiar") }
            return
        }

        val hasAnyError = titleErr != null || targetErr != null || deadlineErr != null
        if (hasAnyError) {
            _uiState.update {
                it.copy(
                    isTitleError = titleErr,
                    isTargetAmountError = targetErr,
                    isDeadlineError = deadlineErr
                )
            }
            return
        }

        val validTarget = parsedTarget ?: return
        val userId = authRepo.getCurrentUserId().id

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val goalData = Goal(
                id = goalToEdit?.id,
                userId = userId,
                title = state.title.trim(),
                targetAmount = validTarget,
                currentAmount = parsedInitial,
                currency = state.currency,
                deadline = state.deadline.trim(),
                accountId = state.accountId,
                categoryId = state.categoryId,
                isCompleted = parsedInitial >= validTarget
            )

            val result = if (goalToEdit != null) {
                goalRepo.updateGoal(goalData)
            } else {
                goalRepo.createGoal(goalData)
            }

            _uiState.update { it.copy(isLoading = false) }

            result.onSuccess {
                resetForm()
                onSuccess()
            }
            result.onFailure { error ->
                _uiState.update {
                    it.copy(errorMessage = error.message ?: "Error al guardar la meta")
                }
            }
        }
    }
}
