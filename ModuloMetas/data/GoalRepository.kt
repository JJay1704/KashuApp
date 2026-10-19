package com.kashuapp.goals.data

import com.kashuapp.data.KashuSupaBase
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

/**
 * Implementación de IGoalRepository sobre Supabase Postgrest.
 *
 * Aplica buenas prácticas de backend:
 * - Filtra en el motor de base de datos mediante 'eq("user_id", userId)'
 * - Maneja excepciones mediante Result.success / Result.failure
 * - Permite operaciones ACID de abono interactivo.
 */
class GoalRepository : IGoalRepository {

    override suspend fun getAllGoals(userId: String): Result<List<Goal>> {
        return try {
            val goals = KashuSupaBase.db.from("goal")
                .select {
                    filter {
                        eq("user_id", userId)
                    }
                }
                .decodeList<Goal>()
            Result.success(goals)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createGoal(goal: Goal): Result<Goal> {
        return try {
            val payload = buildJsonObject {
                goal.userId?.let { put("user_id", it) }
                if (!goal.accountId.isNullOrBlank()) put("account_id", goal.accountId)
                if (!goal.categoryId.isNullOrBlank()) put("category_id", goal.categoryId)
                put("title", goal.title)
                put("target_amount", goal.targetAmount)
                put("current_amount", goal.currentAmount)
                put("currency", goal.currency)
                put("deadline", goal.deadline)
                put("color", goal.color)
                put("icon", goal.icon)
                put("is_completed", goal.isTargetReached)
            }
            val created = KashuSupaBase.db.from("goal")
                .insert(payload) {
                    select()
                }
                .decodeSingle<Goal>()
            Result.success(created)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateGoal(goal: Goal): Result<Unit> {
        return try {
            val goalId = goal.id ?: return Result.failure(IllegalArgumentException("ID de meta inválido"))
            val updatePayload = buildJsonObject {
                put("title", goal.title)
                put("target_amount", goal.targetAmount)
                put("current_amount", goal.currentAmount)
                put("currency", goal.currency)
                put("deadline", goal.deadline)
                put("color", goal.color)
                put("icon", goal.icon)
                put("is_completed", goal.isTargetReached)
                if (goal.accountId != null) put("account_id", goal.accountId)
                if (goal.categoryId != null) put("category_id", goal.categoryId)
            }
            KashuSupaBase.db.from("goal").update(updatePayload) {
                filter {
                    eq("id", goalId)
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
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

    override suspend fun addSavings(goalId: String, amountToAdd: Double): Result<Goal> {
        return try {
            // 1. Obtener la meta actual
            val current = KashuSupaBase.db.from("goal")
                .select {
                    filter { eq("id", goalId) }
                }
                .decodeSingle<Goal>()

            val newCurrentAmount = current.currentAmount + amountToAdd
            val isNowCompleted = newCurrentAmount >= current.targetAmount

            val updatePayload = buildJsonObject {
                put("current_amount", newCurrentAmount)
                put("is_completed", isNowCompleted)
            }

            // 2. Aplicar la actualización
            KashuSupaBase.db.from("goal").update(updatePayload) {
                filter { eq("id", goalId) }
            }

            val updatedGoal = current.copy(
                currentAmount = newCurrentAmount,
                isCompleted = isNowCompleted
            )
            Result.success(updatedGoal)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
