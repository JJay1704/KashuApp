package com.kashuapp.ui.goals.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kashuapp.data.auth.AuthRepository
import com.kashuapp.data.auth.IAuthRepository
import com.kashuapp.data.goal.Goal
import com.kashuapp.data.goal.GoalRepository
import com.kashuapp.data.goal.IGoalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID
import com.kashuapp.core.finance.FinanceRules
import com.kashuapp.data.account.AccountRepository
import com.kashuapp.data.account.IAccountRepository
import com.kashuapp.data.accounType.AccountTypeRepository
import com.kashuapp.data.accounType.IAccountTypeRepository

class GoalsVM(
    private val goalRepo: IGoalRepository = GoalRepository(),
    private val authRepo: IAuthRepository = AuthRepository(),
    private val accountRepo: IAccountRepository = AccountRepository(),
    private val typeRepo: IAccountTypeRepository = AccountTypeRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(GoalsState())
    val uiState = _uiState.asStateFlow()
    private var requestId = UUID.randomUUID().toString()
    private var allGoals: List<Goal> = emptyList()

    fun selectCurrency(currency: String) {
        _uiState.update { it.copy(selectedCurrency = currency) }
        showGoals()
    }

    private fun showGoals() {
        val list = allGoals.filter { it.currency == _uiState.value.selectedCurrency }
        val target = list.sumOf { it.targetAmount }
        val saved = list.sumOf { it.currentAmount }
        val fraction = if (target > 0) (saved / target).toFloat().coerceIn(0f, 1f) else 0f
        _uiState.update { it.copy(goals = list,
            currencies = (allGoals.map { goal -> goal.currency } + it.selectedCurrency).distinct().sorted(),
            totalTargetAmount = target, totalSavedAmount = saved, overallProgressFraction = fraction,
            overallProgressPercentage = (fraction * 100).toInt(),
            completedGoalsCount = list.count { goal -> goal.currentAmount >= goal.targetAmount }) }
    }

    init {
        loadGoals()
    }
    fun getDeadlineAlert(goal: Goal): String {
        if (goal.currentAmount >= goal.targetAmount) {
            return ""
        }

        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val today = dateFormat.format(calendar.time)

        calendar.add(Calendar.DAY_OF_MONTH, 7)
        val warningDate = dateFormat.format(calendar.time)

        return when {
            goal.deadline < today -> "Meta vencida"
            goal.deadline <= warningDate -> "Tu meta está por vencer"
            else -> ""
        }
    }
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
                allGoals = list
                val totalTarget = list.sumOf { it.targetAmount }
                val totalSaved = list.sumOf { it.currentAmount }
                val rawFraction = if (totalTarget > 0.0) {
                    (totalSaved / totalTarget).toFloat()
                } else {
                    0.0f
                }
                val fraction = if (rawFraction > 1.0f) 1.0f else if (rawFraction < 0.0f) 0.0f else rawFraction
                val percentage = (fraction * 100).toInt()
                val completedCount = list.count { it.currentAmount >= it.targetAmount }

                _uiState.update {
                    it.copy(
                        goals = list,
                        totalTargetAmount = totalTarget,
                        totalSavedAmount = totalSaved,
                        overallProgressFraction = fraction,
                        overallProgressPercentage = percentage,
                        completedGoalsCount = completedCount,
                        isLoading = false,
                        errorMessage = null
                    )
                }
                showGoals()
            }

            result.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Error al obtener las metas"
                    )
                }
            }
        }
    }

    fun openCreateGoal() {
        _uiState.update {
            it.copy(
                isFormOpen = true,
                goalToEdit = null,
                errorMessage = null
            )
        }
    }

    fun openEditGoal(goal: Goal) {
        _uiState.update {
            it.copy(
                isFormOpen = true,
                goalToEdit = goal,
                errorMessage = null
            )
        }
    }

    fun closeForm() {
        _uiState.update { it.copy(isFormOpen = false, goalToEdit = null) }
    }

    fun openAddSavings(goal: Goal) {
        requestId = UUID.randomUUID().toString()
        _uiState.update {
            it.copy(
                isAddSavingsOpen = true,
                selectedGoalForSavings = goal,
                depositAmount = "",
                depositAccountId = null,
                accounts = emptyList(),
                errorMessage = null
            )
        }
        viewModelScope.launch {
            val accounts = accountRepo.getAllAccount(authRepo.getCurrentUserId().id)
            val types = typeRepo.getAllAccountTypes()
            if (accounts.isSuccess && types.isSuccess) {
                _uiState.update { it.copy(accounts = accounts.getOrThrow().filter { account ->
                    account.isActive && account.currency == goal.currency &&
                        !FinanceRules.isLiability(account, types.getOrThrow())
                }) }
            } else _uiState.update { it.copy(errorMessage = "No se pudieron cargar las cuentas de origen") }
        }
    }

    fun onDepositAmount(value: String) {
        if (_uiState.value.isLoading) return
        requestId = UUID.randomUUID().toString()
        _uiState.update { it.copy(depositAmount = value, errorMessage = null) }
    }
    fun onDepositAccount(id: String?) {
        if (_uiState.value.isLoading) return
        requestId = UUID.randomUUID().toString()
        _uiState.update { it.copy(depositAccountId = id, errorMessage = null) }
    }

    fun closeAddSavings() {
        if (_uiState.value.isLoading) return
        _uiState.update {
            it.copy(isAddSavingsOpen = false, selectedGoalForSavings = null)
        }
    }

    fun submitSavings(onSuccess: () -> Unit = {}) {
        if (_uiState.value.isLoading) return
        val goal = _uiState.value.selectedGoalForSavings ?: return

        val amount = FinanceRules.positiveAmount(_uiState.value.depositAmount)
        if (amount == null || amount <= 0.0) {
            _uiState.update { it.copy(errorMessage = "El monto a abonar debe ser mayor a 0") }
            return
        }

        val account = _uiState.value.accounts.find { it.id == _uiState.value.depositAccountId }
        if (account == null || !account.isActive || account.currency != goal.currency || account.currentBalance < amount) {
            _uiState.update { it.copy(errorMessage = "Selecciona una cuenta activa de la misma moneda con saldo suficiente") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = goalRepo.contribute(goal.id ?: return@launch, account.id!!, amount, requestId)
            result.onSuccess {
                _uiState.update { it.copy(isLoading = false) }
                closeAddSavings()
                loadGoals()
                onSuccess()
            }
            result.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Error al registrar el abono"
                    )
                }
            }
        }
    }
}
