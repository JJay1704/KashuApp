package com.kashuapp

import com.kashuapp.core.finance.FinanceRules
import com.kashuapp.data.account.Account
import com.kashuapp.data.accounType.AccountType
import com.kashuapp.data.goal.Goal
import org.junit.Assert.*
import org.junit.Test
import java.util.TimeZone

class FinanceRulesTest {
    @Test fun rejectsNonFiniteAndNonPositiveMoney() {
        listOf("NaN", "Infinity", "-Infinity", "0", "-1", "abc").forEach {
            assertNull(it, FinanceRules.positiveAmount(it))
        }
        assertEquals(12.5, FinanceRules.positiveAmount("12,50")!!, 0.001)
    }

    @Test fun rangeIncludesLeapDayAndRejectsReversedOrImpossibleDates() {
        val originalZone = TimeZone.getDefault()
        try {
            TimeZone.setDefault(TimeZone.getTimeZone("America/Lima"))
            assertEquals("2024-03-01T00:00:00-05:00", FinanceRules.dateRange("2024-02-01", "2024-02-29")!!.second)
            assertNull(FinanceRules.dateRange("2025-02-29", "2025-03-01"))
            assertNull(FinanceRules.dateRange("2026-10-20", "2026-10-01"))
            assertEquals("2027-01-01T00:00:00-05:00", FinanceRules.monthRange(2026, 12).second)
        } finally { TimeZone.setDefault(originalZone) }
    }

    @Test fun keepsCurrenciesSeparateAndExcludesInactiveBalances() {
        val types = listOf(AccountType("bank", "Banco", "activo"), AccountType("loan", "Préstamo", "pasivo"))
        val accounts = listOf(Account(name = "Banco", type = "bank", currentBalance = 100.0),
            Account(name = "Deuda", type = "loan", currentBalance = 40.0),
            Account(name = "Inactiva", type = "bank", currentBalance = 500.0, isActive = false),
            Account(name = "USD", type = "bank", currentBalance = 10.0, currency = "USD"))
        val pen = FinanceRules.balances(accounts, types).first { it.currency == "PEN" }
        assertEquals(100.0, pen.assets, 0.001)
        assertEquals(60.0, pen.netWorth, 0.001)
        assertEquals(10.0, FinanceRules.balances(accounts, types).first { it.currency == "USD" }.netWorth, 0.001)
    }

    @Test fun reservingSavingsDoesNotDestroyNetWorth() {
        val types = listOf(AccountType("bank", "Banco", "activo"))
        val goal = Goal(title = "Viaje", targetAmount = 100.0, currentAmount = 30.0, deadline = "2027-01-01")
        val balance = FinanceRules.balances(listOf(Account(name = "Banco", type = "bank", currentBalance = 70.0)), types, listOf(goal)).single()
        assertEquals(70.0, balance.assets, 0.001)
        assertEquals(100.0, balance.netWorth, 0.001)
    }

    @Test fun alertThresholdsIncludeExactBoundaries() {
        assertEquals(0, FinanceRules.budgetThreshold(79.99, 100.0))
        assertEquals(80, FinanceRules.budgetThreshold(80.0, 100.0))
        assertEquals(100, FinanceRules.budgetThreshold(100.0, 100.0))
        assertEquals(100, FinanceRules.budgetThreshold(120.0, 100.0))
    }
}
