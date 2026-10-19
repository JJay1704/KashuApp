package com.kashuapp.data.goal

interface IGoalRepository {

    suspend fun getAllGoals(userId: String): Result<List<Goal>>
    suspend fun createGoal(goal: Goal): Result<Unit>
    suspend fun updateGoal(goal: Goal): Result<Unit>
    suspend fun deleteGoal(goalId: String): Result<Unit>

}
