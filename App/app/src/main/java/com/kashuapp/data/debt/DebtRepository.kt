package com.kashuapp.data.debt

import com.kashuapp.data.KashuSupaBase
import kotlinx.coroutines.CancellationException

class DebtRepository : IDebtRepository {
    override suspend fun getDebts(userId: String): Result<List<Debt>> = request {
        KashuSupaBase.db.from("debt").select {
            filter { eq("user_id", userId) }
        }.decodeList<Debt>()
    }

    override suspend fun getPayments(userId: String): Result<List<DebtPayment>> = request {
        KashuSupaBase.db.from("debt_payment").select {
            filter { eq("user_id", userId) }
        }.decodeList<DebtPayment>()
    }

    override suspend fun createDebt(debt: Debt): Result<Unit> = request {
        val existing = KashuSupaBase.db.from("debt").select {
            filter { eq("id", debt.id) }
        }.decodeList<Debt>().singleOrNull()
        if (existing == null) {
            KashuSupaBase.db.from("debt").insert(debt)
        } else {
            check(existing.userId == debt.userId && existing.name == debt.name &&
                existing.creditor.orEmpty() == debt.creditor.orEmpty() && existing.currency == debt.currency &&
                existing.initialAmount == debt.initialAmount) { "El identificador ya pertenece a otra deuda" }
        }
        Unit
    }

    override suspend fun updateDetails(id: String, name: String, creditor: String): Result<Unit> = request {
        val updated = KashuSupaBase.db.from("debt").update({
            set("name", name)
            set("creditor", creditor)
        }) {
            filter { eq("id", id) }
            select()
        }.decodeList<Debt>()
        check(updated.size == 1) { "La deuda ya no está disponible. Actualiza la lista" }
        Unit
    }

    override suspend fun deleteDebt(id: String): Result<Unit> = request {
        KashuSupaBase.db.from("debt").delete {
            filter { eq("id", id) }
        }
        Unit
    }

    override suspend fun payDebt(payment: DebtPayment): Result<Unit> = request {
        // El trigger de debt_payment valida y descuenta ambos saldos en la misma transacción.
        KashuSupaBase.db.from("debt_payment").insert(payment)
        Unit
    }

    private suspend fun <T> request(block: suspend () -> T): Result<T> = try {
        Result.success(block())
    } catch (error: CancellationException) {
        throw error
    } catch (error: Exception) {
        val detail = error.message.orEmpty()
        val rejection = when {
            "DEBT_ACCOUNT_INVALID" in detail -> "Selecciona una cuenta activa propia de la misma moneda"
            "DEBT_INSUFFICIENT_FUNDS" in detail -> "La cuenta no tiene saldo suficiente para este abono"
            "DEBT_AMOUNT_EXCEEDS_BALANCE" in detail -> "El abono supera el pendiente actual de la deuda"
            "DEBT_PAYMENT_CONFLICT" in detail -> "Este identificador ya se usó para otro abono"
            "DEBT_INVALID_OWNER" in detail -> "La deuda no pertenece a la sesión actual"
            "DEBT_INVALID_INITIAL" in detail -> "El saldo inicial debe coincidir con el pendiente"
            "DEBT_INVALID_AMOUNT" in detail -> "Ingresa un monto válido mayor que cero"
            "23503" in detail -> "No se puede completar: el registro está relacionado con otros datos"
            "42501" in detail -> "No tienes permiso para completar esta operación"
            else -> null
        }
        Result.failure(DebtOperationException(rejection ?: "No se pudo completar la operación. Actualiza antes de reintentar.",
            confirmedRejected = rejection != null, cause = error))
    }
}

internal class DebtOperationException(message: String, val confirmedRejected: Boolean, cause: Throwable) :
    Exception(message, cause)
