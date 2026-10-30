package com.kashuapp.data.goal

import com.kashuapp.data.KashuSupaBase
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

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
            KashuSupaBase.db.rpc("delete_goal_with_refund", buildJsonObject { put("p_goal_id", goalId) })
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
            KashuSupaBase.db.from("goal").update({
                set("title", goal.title)
                set("target_amount", goal.targetAmount)
                set("deadline", goal.deadline)
                set("account_id", goal.accountId)
                set("category_id", goal.categoryId)
            }) {
                filter {
                    eq("id", goal.id ?: "")
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun contribute(goalId: String, accountId: String, amount: Double, requestId: String): Result<Unit> =
        runCatching {
            KashuSupaBase.db.rpc("contribute_to_goal", buildJsonObject {
                put("p_goal_id", goalId)
                put("p_account_id", accountId)
                put("p_amount", amount)
                put("p_request_id", requestId)
            })
            Unit
        }

}
