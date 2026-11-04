package com.kashuapp.data.debt

import com.kashuapp.data.KashuSupaBase
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

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
        KashuSupaBase.db.rpc("debt_create", buildJsonObject {
            put("p_id", debt.id)
            put("p_name", debt.name)
            put("p_creditor", debt.creditor.orEmpty())
            put("p_currency", debt.currency)
            put("p_amount", debt.initialAmount.toBigDecimal().toPlainString())
        })
        Unit
    }

    override suspend fun updateDetails(id: String, name: String, creditor: String): Result<Unit> = request {
        KashuSupaBase.db.rpc("debt_update_details", buildJsonObject {
            put("p_id", id)
            put("p_name", name)
            put("p_creditor", creditor)
        })
        Unit
    }

    override suspend fun deleteDebt(id: String): Result<Unit> = request {
        KashuSupaBase.db.rpc("debt_delete", buildJsonObject { put("p_id", id) })
        Unit
    }

    override suspend fun payDebt(requestId: String, debtId: String, accountId: String,
                                 amount: String, paidAt: String, description: String): Result<Unit> = request {
        KashuSupaBase.db.rpc("debt_pay", buildJsonObject {
            put("p_request_id", requestId)
            put("p_debt_id", debtId)
            put("p_account_id", accountId)
            put("p_amount", amount)
            put("p_paid_at", paidAt)
            put("p_description", description)
        })
        Unit
    }

    private suspend fun <T> request(block: suspend () -> T): Result<T> = try {
        Result.success(block())
    } catch (error: CancellationException) {
        throw error
    } catch (error: Exception) {
        Result.failure(error)
    }
}
