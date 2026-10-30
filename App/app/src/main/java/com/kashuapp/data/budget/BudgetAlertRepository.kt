package com.kashuapp.data.budget

import com.kashuapp.core.finance.FinanceRules
import com.kashuapp.data.account.AccountRepository
import com.kashuapp.data.account.IAccountRepository
import com.kashuapp.data.transaction.ITransactionRepository
import com.kashuapp.data.transaction.Transaction
import com.kashuapp.data.transaction.TransactionRepository

interface IBudgetAlertRepository {
    suspend fun afterSave(userId: String, movement: Transaction, previous: Transaction?): Result<String?>
}

class BudgetAlertRepository(
    private val budgets: IBudgetRepository = BudgetRepository(),
    private val transactions: ITransactionRepository = TransactionRepository(),
    private val accounts: IAccountRepository = AccountRepository()
) : IBudgetAlertRepository {
    override suspend fun afterSave(userId: String, movement: Transaction, previous: Transaction?): Result<String?> = runCatching {
        if (movement.type != "EXPENSE") return@runCatching null
        val date = FinanceRules.localDate(movement.transactionDate)
        val year = date.take(4).toInt()
        val month = date.substring(5, 7).toInt()
        val accountList = accounts.getAllAccount(userId).getOrThrow()
        val currency = accountList.first { it.id == movement.accountId }.currency
        val ids = accountList.filter { it.currency == currency }.mapNotNull { it.id }.toSet()
        val budgetList = budgets.getBudgets(userId, month, year).getOrThrow()
            .filter { it.categoryId == movement.categoryId && it.currency == currency }
        val (start, end) = FinanceRules.monthRange(year, month)
        val spent = transactions.getAll(userId, null, movement.categoryId, null, start, end, "EXPENSE")
            .getOrThrow().filter { it.accountId in ids }.sumOf { it.amount }
        val previousAmount = previous?.takeIf {
            it.type == "EXPENSE" && it.categoryId == movement.categoryId && it.accountId in ids &&
                FinanceRules.localDate(it.transactionDate).take(7) == date.take(7)
        }?.amount ?: 0.0
        val before = spent - movement.amount + previousAmount
        val alerts = budgetList.mapNotNull { budget ->
            val threshold = FinanceRules.budgetThreshold(spent, budget.amountLimit)
            if (threshold > FinanceRules.budgetThreshold(before, budget.amountLimit))
                "Tu presupuesto de $currency ${budget.amountLimit} alcanzó el $threshold% (gastado: $currency ${String.format("%.2f", spent)})."
            else null
        }
        alerts.takeIf { it.isNotEmpty() }?.joinToString("\n")
    }
}
