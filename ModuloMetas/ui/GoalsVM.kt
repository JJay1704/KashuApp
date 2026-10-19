package com.kashuapp.goals.ui

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
import com.kashuapp.goals.data.Goal
import com.kashuapp.goals.data.GoalRepository
import com.kashuapp.goals.data.IGoalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel para la gestión reactiva de Metas de Ahorro.
 *
 * Cumple con:
 * - StateFlow inmutable expuesto a la UI (Clean Architecture / MVVM)
 * - Carga asíncrona de catálogos (Cuentas y Categorías) para selección opcional
 * - Soporte para guardar metas sin cuenta ni categoría (Alcancía Virtual)
 * - Validaciones en tiempo real de campos obligatorios y montos (> 0)
 * - Abonos interactivos inmediatos
 */
class GoalsVM(
    private val goalRepo: IGoalRepository = GoalRepository(),
    private val authRepo: IAuthRepository = AuthRepository(),
    private val accountRepo: IAccountRepository = AccountRepository(),
    private val catRepo: ICategoryRepository = CategoryRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(GoalsState())
    val uiState = _uiState.asStateFlow()

    private val _formState = MutableStateFlow(GoalFormState())
    val formState = _formState.asStateFlow()

    init {
        loadGoals()
    }

    /**
     * Carga las metas asociadas al usuario autenticado actual.
     */
    fun loadGoals() {
        val userId = authRepo.getCurrentUserId().id
        if (userId == "Not Gotten" || userId.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Sesión no válida o expirada") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = goalRepo.getAllGoals(userId)
            result.onSuccess { list ->
                _uiState.update {
                    it.copy(
                        goals = list,
                        isLoading = false,
                        errorMessage = null
                    )
                }
            }
            result.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = error.localizedMessage ?: "Error al obtener las metas"
                    )
                }
            }
        }
    }

    // ==========================================
    // Control de Formularios (Crear / Editar)
    // ==========================================

    fun openCreateGoal() {
        _formState.value = GoalFormState()
        _uiState.update { it.copy(isFormOpen = true, goalToEdit = null, errorMessage = null) }
        loadCatalogs()
    }

    fun openEditGoal(goal: Goal) {
        _formState.value = GoalFormState(
            title = goal.title,
            targetAmount = goal.targetAmount.toString(),
            initialAmount = goal.currentAmount.toString(),
            deadline = goal.deadline,
            currency = goal.currency,
            color = goal.color,
            icon = goal.icon,
            selectedAccountId = goal.accountId,
            selectedCategoryId = goal.categoryId
        )
        _uiState.update { it.copy(isFormOpen = true, goalToEdit = goal, errorMessage = null) }
        loadCatalogs(selectedAccountId = goal.accountId, selectedCategoryId = goal.categoryId)
    }

    private fun loadCatalogs(selectedAccountId: String? = null, selectedCategoryId: String? = null) {
        val userId = authRepo.getCurrentUserId().id
        viewModelScope.launch {
            val accList = accountRepo.getAllAccount(userId).getOrDefault(emptyList())
            val catList = catRepo.getAllCateg(userId).getOrDefault(emptyList())

            val matchedAccount = accList.find { it.id == selectedAccountId }
            val matchedCategory = catList.find { it.id == selectedCategoryId }

            _formState.update { current ->
                current.copy(
                    availableAccounts = accList,
                    availableCategories = catList,
                    selectedAccountName = matchedAccount?.name ?: current.selectedAccountName,
                    selectedCategoryName = matchedCategory?.name ?: current.selectedCategoryName,
                    currency = matchedAccount?.currency ?: current.currency
                )
            }
        }
    }

    fun closeForm() {
        _uiState.update { it.copy(isFormOpen = false, goalToEdit = null) }
    }

    fun onFormTitleChange(newTitle: String) {
        val error = if (newTitle.trim().isBlank()) "El nombre es obligatorio" else null
        _formState.update { it.copy(title = newTitle, titleError = error) }
    }

    fun onFormTargetAmountChange(newAmount: String) {
        val parsed = newAmount.toDoubleOrNull()
        val error = when {
            newAmount.isBlank() -> "Ingrese un monto objetivo"
            parsed == null || parsed <= 0.0 -> "El monto debe ser mayor a 0"
            else -> null
        }
        _formState.update { it.copy(targetAmount = newAmount, targetAmountError = error) }
    }

    fun onFormInitialAmountChange(newAmount: String) {
        _formState.update { it.copy(initialAmount = newAmount) }
    }

    fun onFormDeadlineChange(newDeadline: String) {
        val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
        val error = when {
            newDeadline.trim().isBlank() -> "La fecha límite es obligatoria"
            newDeadline < today -> "La fecha límite debe ser hoy o una fecha futura"
            else -> null
        }
        _formState.update { it.copy(deadline = newDeadline, deadlineError = error) }
    }

    fun onFormIconChange(newIcon: String) {
        _formState.update { it.copy(icon = newIcon) }
    }

    /**
     * Selecciona una cuenta para la meta o null para dejarla como Alcancía Virtual.
     */
    fun onSelectAccount(account: Account?) {
        _formState.update {
            it.copy(
                selectedAccountId = account?.id,
                selectedAccountName = account?.name,
                currency = account?.currency ?: "PEN"
            )
        }
    }

    /**
     * Selecciona una categoría para la meta o null para dejarla general.
     */
    fun onSelectCategory(category: Category?) {
        _formState.update {
            it.copy(
                selectedCategoryId = category?.id,
                selectedCategoryName = category?.name
            )
        }
    }

    fun saveGoal(onSuccess: () -> Unit = {}) {
        val currentForm = _formState.value
        val parsedTarget = currentForm.targetAmount.toDoubleOrNull()
        val parsedInitial = currentForm.initialAmount.toDoubleOrNull() ?: 0.0
        val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())

        val titleErr = if (currentForm.title.trim().isBlank()) "El nombre es obligatorio" else null
        val targetErr = if (parsedTarget == null || parsedTarget <= 0.0) "Monto mayor a 0 obligatorio" else null
        val deadlineErr = when {
            currentForm.deadline.trim().isBlank() -> "Fecha límite obligatoria"
            currentForm.deadline < today -> "La fecha límite debe ser hoy o una fecha futura"
            else -> null
        }

        if (titleErr != null || targetErr != null || deadlineErr != null || parsedTarget == null) {
            _formState.update {
                it.copy(
                    titleError = titleErr,
                    targetAmountError = targetErr,
                    deadlineError = deadlineErr
                )
            }
            return
        }

        val validTargetAmount = parsedTarget

        val userId = authRepo.getCurrentUserId().id
        if (userId == "Not Gotten" || userId.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Sesión no válida o expirada. Vuelve a iniciar sesión.") }
            return
        }
        viewModelScope.launch {
            _formState.update { it.copy(isSubmitting = true) }
            val editingGoal = _uiState.value.goalToEdit

            val result = if (editingGoal != null) {
                val updated = editingGoal.copy(
                    title = currentForm.title.trim(),
                    targetAmount = validTargetAmount,
                    currentAmount = parsedInitial,
                    currency = currentForm.currency,
                    deadline = currentForm.deadline.trim(),
                    color = currentForm.color,
                    icon = currentForm.icon,
                    accountId = currentForm.selectedAccountId,
                    categoryId = currentForm.selectedCategoryId,
                    isCompleted = parsedInitial >= validTargetAmount
                )
                goalRepo.updateGoal(updated)
            } else {
                val newGoal = Goal(
                    userId = userId,
                    title = currentForm.title.trim(),
                    targetAmount = validTargetAmount,
                    currentAmount = parsedInitial,
                    currency = currentForm.currency,
                    deadline = currentForm.deadline.trim(),
                    color = currentForm.color,
                    icon = currentForm.icon,
                    accountId = currentForm.selectedAccountId,
                    categoryId = currentForm.selectedCategoryId,
                    isCompleted = parsedInitial >= validTargetAmount
                )
                goalRepo.createGoal(newGoal).map { Unit }
            }

            _formState.update { it.copy(isSubmitting = false) }
            result.onSuccess {
                closeForm()
                loadGoals()
                _uiState.update { it.copy(successMessage = "Meta guardada con éxito") }
                onSuccess()
            }
            result.onFailure { error ->
                _uiState.update {
                    it.copy(errorMessage = error.localizedMessage ?: "Error al guardar la meta")
                }
            }
        }
    }

    fun deleteGoal(goalId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = goalRepo.deleteGoal(goalId)
            result.onSuccess {
                loadGoals()
                _uiState.update { it.copy(successMessage = "Meta eliminada correctamente") }
            }
            result.onFailure { error ->
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = error.localizedMessage ?: "Error al eliminar")
                }
            }
        }
    }

    // ==========================================
    // Abono / Ahorro Interactivo en Tiempo Real
    // ==========================================

    fun openAddSavings(goal: Goal) {
        _uiState.update {
            it.copy(
                isAddSavingsOpen = true,
                selectedGoalForSavings = goal,
                errorMessage = null
            )
        }
    }

    fun closeAddSavings() {
        _uiState.update {
            it.copy(isAddSavingsOpen = false, selectedGoalForSavings = null)
        }
    }

    fun submitSavings(amountStr: String, onSuccess: () -> Unit = {}) {
        val goal = _uiState.value.selectedGoalForSavings ?: return
        val amount = amountStr.toDoubleOrNull()
        if (amount == null || amount <= 0.0) {
            _uiState.update { it.copy(errorMessage = "El monto a abonar debe ser mayor a 0") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = goalRepo.addSavings(goal.id ?: return@launch, amount)
            result.onSuccess {
                closeAddSavings()
                loadGoals()
                _uiState.update {
                    it.copy(
                        successMessage = "¡Abono registrado! Progreso actualizado."
                    )
                }
                onSuccess()
            }
            result.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = error.localizedMessage ?: "Error al registrar el abono"
                    )
                }
            }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }
}
