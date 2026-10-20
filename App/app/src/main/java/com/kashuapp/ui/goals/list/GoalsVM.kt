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

class GoalsVM(
    private val goalRepo: IGoalRepository = GoalRepository(),
    private val authRepo: IAuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(GoalsState())
    val uiState = _uiState.asStateFlow()

    init {
        loadGoals()
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
                val totalTarget = list.sumOf { it.targetAmount }
                val totalSaved = list.sumOf { it.currentAmount }
                val rawFraction = if (totalTarget > 0.0) {
                    (totalSaved / totalTarget).toFloat()
                } else {
                    0.0f
                }
                val fraction =
                    if (rawFraction > 1.0f) 1.0f else if (rawFraction < 0.0f) 0.0f else rawFraction
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
                isFormOpen = true, goalToEdit = null, errorMessage = null
            )
        }
    }

    fun openEditGoal(goal: Goal) {
        _uiState.update {
            it.copy(
                isFormOpen = true, goalToEdit = goal, errorMessage = null
            )
        }
    }

    fun closeForm() {
        _uiState.update { it.copy(isFormOpen = false, goalToEdit = null) }
    }

    fun openAddSavings(goal: Goal) {
        _uiState.update {
            it.copy(
                isAddSavingsOpen = true, selectedGoalForSavings = goal, errorMessage = null
            )
        }
    }



    fun checkTargetsReached() {
        val updatedList = mutableListOf<Goal>()
        var completedCount = 0

        for (i in _uiState.value.goals) {
            val isCompleted = i.currentAmount >= i.targetAmount

            if (isCompleted) {
                completedCount++
            }

            val updatedGoal = i.copy(isCompleted = isCompleted)
            updatedList.add(updatedGoal)
        }

        _uiState.update { currentState ->
            currentState.copy(
                goals = updatedList,
                completedGoalsCount = completedCount
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

        val differenceAmounts = goal.targetAmount - goal.currentAmount



        if (amount == null || amount <= 0.0) {
            _uiState.update { it.copy(errorMessage = "El monto a abonar debe ser mayor a 0") }
            return

        } else {
            if (differenceAmounts >= amount) {


                val newCurrentAmount = goal.currentAmount + amount
                val updatedGoal = goal.copy(
                    currentAmount = newCurrentAmount,
                    isCompleted = newCurrentAmount >= goal.targetAmount
                )

                viewModelScope.launch {
                    _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                    val result = goalRepo.updateGoal(updatedGoal)
                    result.onSuccess {
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


    }
}



