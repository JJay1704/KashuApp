package com.kashuapp.data.goal

import com.kashuapp.data.KashuSupaBase

class GoalRepository : IGoalRepository {

    override suspend fun getAllGoals(userId: String): Result<List<Goal>> {
        try {
            val goalTable = KashuSupaBase.db.from("goal").select().decodeList<Goal>()
            val filtered = goalTable.filter { goal ->
                goal.userId == null || goal.userId == userId
            }
            return Result.success(filtered)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    override suspend fun deleteGoal(goalId: String): Result<Unit> {
        return try {
            KashuSupaBase.db.from("goal").delete {
                filter {
                    eq("id", goalId)
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createGoal(goal: Goal): Result<Unit> {
        return try {
            KashuSupaBase.db.from("goal").insert(goal)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateGoal(goal: Goal): Result<Unit> {
        return try {
            KashuSupaBase.db.from("goal").update(goal) {
                filter {
                    eq("id", goal.id ?: "")
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}
