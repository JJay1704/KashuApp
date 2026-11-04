package com.kashuapp.data.debt

interface IDebtRepository {
    suspend fun getDebts(userId: String): Result<List<Debt>>
    suspend fun getPayments(userId: String): Result<List<DebtPayment>>
    suspend fun createDebt(debt: Debt): Result<Unit>
    suspend fun updateDetails(id: String, name: String, creditor: String): Result<Unit>
    suspend fun deleteDebt(id: String): Result<Unit>
    suspend fun payDebt(requestId: String, debtId: String, accountId: String,
                        amount: String, paidAt: String, description: String): Result<Unit>
}
