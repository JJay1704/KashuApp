package com.kashuapp.data.budget

import com.kashuapp.data.KashuSupaBase

class BudgetRepository : IBudgetRepository {

    override suspend fun getBudgets(userId: String, month: Int, year: Int): Result<List<Budget>> {
        try {
            val budgetTable = KashuSupaBase.db.from("budget").select().decodeList<Budget>()
            val filtered = budgetTable.filter { budget ->
                (budget.userId == null || budget.userId == userId) &&
                        budget.month == month &&
                        budget.year == year
            }
            return Result.success(filtered)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    override suspend fun deleteBudget(budgetId: String): Result<Unit> {
        return try {
            KashuSupaBase.db.from("budget").delete {
                filter {
                    eq("id", budgetId)
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createBudget(budget: Budget): Result<Unit> {
        return try {
            KashuSupaBase.db.from("budget").insert(budget)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateBudget(budget: Budget): Result<Unit> {
        return try {
            KashuSupaBase.db.from("budget").update(budget) {
                filter {
                    eq("id", budget.id ?: "")
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}
