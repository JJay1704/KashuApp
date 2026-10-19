package com.kashuapp.goals.data

/**
 * Contrato de persistencia para el módulo de Metas de Ahorro.
 * Sigue el principio de Inversión de Dependencias (D de SOLID),
 * permitiendo sustituir la fuente de datos (Supabase, Room o Mock)
 * sin modificar la lógica de los ViewModels.
 */
interface IGoalRepository {

    /**
     * Obtiene la lista de metas asociadas al usuario autenticado.
     */
    suspend fun getAllGoals(userId: String): Result<List<Goal>>

    /**
     * Registra una nueva meta de ahorro con validaciones de monto y fecha.
     */
    suspend fun createGoal(goal: Goal): Result<Goal>

    /**
     * Actualiza la información básica de una meta existente.
     */
    suspend fun updateGoal(goal: Goal): Result<Unit>

    /**
     * Elimina permanentemente una meta por su identificador.
     */
    suspend fun deleteGoal(goalId: String): Result<Unit>

    /**
     * Operación interactiva: Abona un monto de ahorro a la meta, recalculando su estado.
     */
    suspend fun addSavings(goalId: String, amountToAdd: Double): Result<Goal>
}
