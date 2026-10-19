package com.kashuapp.data.budget

interface IBudgetRepository {

    suspend fun getBudgets(userId: String, month: Int, year: Int): Result<List<Budget>>
    suspend fun createBudget(budget: Budget): Result<Unit>
    suspend fun updateBudget(budget: Budget): Result<Unit>
    suspend fun deleteBudget(budgetId: String): Result<Unit>

}
