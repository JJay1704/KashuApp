package com.kashuapp.data.debt

import com.kashuapp.data.KashuSupaBase

class DebtRepository : IDebtRepository {

    override suspend fun getDebts(userId: String): Result<List<Debt>> {
        return try {
            val debts = KashuSupaBase.db.from("debt").select {
                filter { eq("user_id", userId) }
            }.decodeList<Debt>()
            Result.success(debts)
        } catch (e: Exception) {
            Result.failure(mapDebtError(e))
        }
    }

    override suspend fun getPayments(userId: String): Result<List<DebtPayment>> {
        return try {
            val payments = KashuSupaBase.db.from("debt_payment").select {
                filter { eq("user_id", userId) }
            }.decodeList<DebtPayment>()
            Result.success(payments)
        } catch (e: Exception) {
            Result.failure(mapDebtError(e))
        }
    }

    override suspend fun createDebt(debt: Debt): Result<Unit> {
        return try {
            KashuSupaBase.db.from("debt").insert(debt)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(mapDebtError(e))
        }
    }

    override suspend fun updateDetails(id: String, name: String, creditor: String): Result<Unit> {
        return try {
            KashuSupaBase.db.from("debt").update({
                set("name", name)
                set("creditor", creditor)
            }) {
                filter { eq("id", id) }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(mapDebtError(e))
        }
    }

    override suspend fun deleteDebt(id: String): Result<Unit> {
        return try {
            KashuSupaBase.db.from("debt").delete {
                filter { eq("id", id) }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(mapDebtError(e))
        }
    }

    override suspend fun payDebt(payment: DebtPayment): Result<Unit> {
        return try {
            KashuSupaBase.db.from("debt_payment").insert(payment)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(mapDebtError(e))
        }
    }

    private fun mapDebtError(error: Exception): Exception {
        val detail = error.message.orEmpty()
        val message = when {
            "DEBT_ACCOUNT_INVALID" in detail -> "Selecciona una cuenta activa propia de la misma moneda"
            "DEBT_INSUFFICIENT_FUNDS" in detail -> "La cuenta no tiene saldo suficiente para este abono"
            "DEBT_AMOUNT_EXCEEDS_BALANCE" in detail -> "El abono supera el pendiente actual de la deuda"
            "DEBT_INVALID_OWNER" in detail -> "La deuda no pertenece a la sesión actual"
            "DEBT_INVALID_INITIAL" in detail -> "El saldo inicial debe coincidir con el pendiente"
            "DEBT_INVALID_AMOUNT" in detail -> "Ingresa un monto válido mayor que cero"
            else -> "No se pudo completar la operación"
        }
        return Exception(message, error)
    }
}
