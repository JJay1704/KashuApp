package com.kashuapp.data.budget

import com.kashuapp.data.KashuSupaBase

class BudgetRepository : IBudgetRepository {

    override suspend fun getBudgets(userId: String, month: Int, year: Int): Result<List<Budget>> {
        return try {
            val budgets = KashuSupaBase.db.from("budget").select {
                filter {
                    eq("user_id", userId)
                    eq("month", month)
                    eq("year", year)
                }
            }.decodeList<Budget>()
            Result.success(budgets)
        } catch (e: Exception) {
            Result.failure(e)
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
