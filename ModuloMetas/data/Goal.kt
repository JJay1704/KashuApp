package com.kashuapp.goals.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Entidad de dominio y transporte para las Metas de Ahorro (HU-07 / RF-07).
 *
 * Incluye propiedades serializables para Supabase y getters calculados
 * para la visualización gráfica inmediata del progreso y saldo faltante (RF-07.1).
 */
@Serializable
data class Goal(
    val id: String? = null,
    @SerialName("user_id") val userId: String? = null,
    @SerialName("account_id") val accountId: String? = null,     // Opcional: cuenta asociada
    @SerialName("category_id") val categoryId: String? = null,   // Opcional: categoría asociada
    val title: String,
    @SerialName("target_amount") val targetAmount: Double,
    @SerialName("current_amount") val currentAmount: Double = 0.0,
    val currency: String = "PEN",
    val deadline: String,
    val color: String = "#34D399",
    val icon: String = "savings",
    @SerialName("is_completed") val isCompleted: Boolean = false,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null
) {
    /**
     * Fracción normalizada de 0.0f a 1.0f para animar la barra de progreso en Jetpack Compose.
     */
    val progressFraction: Float
        get() = if (targetAmount > 0.0) {
            (currentAmount / targetAmount).toFloat().coerceIn(0.0f, 1.0f)
        } else {
            0.0f
        }

    /**
     * Porcentaje entero entre 0% y 100%+ para renderizar texto visual (ej. "45%").
     */
    val progressPercentage: Int
        get() = if (targetAmount > 0.0) {
            ((currentAmount / targetAmount) * 100).toInt().coerceAtLeast(0)
        } else {
            0
        }

    /**
     * Monto faltante exacto para alcanzar el objetivo (RF-07.1).
     */
    val remainingAmount: Double
        get() = maxOf(0.0, targetAmount - currentAmount)

    /**
     * Determina si la meta ya fue alcanzada o superada.
     */
    val isTargetReached: Boolean
        get() = currentAmount >= targetAmount

    /**
     * Símbolo monetario correspondiente a la divisa configurada.
     */
    val currencySymbol: String
        get() = when (currency.uppercase()) {
            "USD" -> "$"
            "EUR" -> "€"
            else -> "S/"
        }
}
