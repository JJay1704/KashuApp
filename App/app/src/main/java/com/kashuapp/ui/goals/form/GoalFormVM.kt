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

class GoalFormVM(
    private val goalRepo: IGoalRepository = GoalRepository(),
    private val authRepo: IAuthRepository = AuthRepository(),
    private val accountRepo: IAccountRepository = AccountRepository(),
    private val catRepo: ICategoryRepository = CategoryRepository()
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

            val matchedAccount = accList.find { it.id == _uiState.value.accountId }
            val matchedCategory = catList.find { it.id == _uiState.value.categoryId }

            _uiState.update { current ->
                current.copy(
                    accounts = accList,
                    categories = catList,
                    accountName = matchedAccount?.name ?: current.accountName,
                    categoryName = matchedCategory?.name ?: current.categoryName,
                    currency = matchedAccount?.currency ?: current.currency
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
                println(error)
            }
        }
    }

    fun saveGoal(goalToEdit: Goal?, onSuccess: () -> Unit) {
        val state = _uiState.value
        val parsedTarget = state.targetAmount.toDoubleOrNull()
        val parsedInitial = state.initialAmount.toDoubleOrNull() ?: 0.0
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        val titleErr = if (state.title.trim().isBlank()) "El nombre es obligatorio" else null
        val targetErr = if (parsedTarget == null || parsedTarget <= 0.0) "Ingrese monto válido mayor a 0" else null
        val deadlineErr = when {
            state.deadline.trim().isBlank() -> "La fecha límite es obligatoria"
            state.deadline < today -> "La fecha límite debe ser hoy o futura"
            else -> null
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