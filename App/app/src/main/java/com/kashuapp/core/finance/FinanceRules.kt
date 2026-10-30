package com.kashuapp.core.finance

import com.kashuapp.data.account.Account
import com.kashuapp.data.accounType.AccountType
import com.kashuapp.data.goal.Goal
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import java.math.BigDecimal

data class CurrencyBalance(val currency: String, val assets: Double, val liabilities: Double, val reserved: Double = 0.0) {
    val netWorth: Double get() = assets + reserved - liabilities
}

object FinanceRules {
    fun positiveAmount(text: String): Double? {
        val normalized = text.trim().replace(',', '.')
        val decimal = normalized.toBigDecimalOrNull() ?: return null
        if (decimal.stripTrailingZeros().scale() > 2) return null
        return decimal.toDouble().takeIf { it.isFinite() && it > 0.0 && it <= 999999999999.99 }
    }

    fun isLiability(account: Account, types: List<AccountType>): Boolean =
        types.any { it.id == account.type && it.classification == "pasivo" }

    fun balances(accounts: List<Account>, types: List<AccountType>, goals: List<Goal> = emptyList()): List<CurrencyBalance> =
        (accounts.filter { it.isActive }.map { it.currency } + goals.map { it.currency }).distinct().map { currency ->
            val group = accounts.filter { it.isActive && it.currency == currency }
            CurrencyBalance(currency,
                group.filterNot { isLiability(it, types) }.sumOf { it.currentBalance },
                group.filter { isLiability(it, types) }.sumOf { it.currentBalance },
                goals.filter { it.currency == currency }.sumOf { it.currentAmount })
        }

    // Half-open ranges include the whole last day, including seconds and milliseconds.
    fun dateRange(start: String, endInclusive: String): Pair<String, String>? {
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply { isLenient = false }
        return runCatching {
            require(start.matches(Regex("\\d{4}-\\d{2}-\\d{2}")))
            require(endInclusive.matches(Regex("\\d{4}-\\d{2}-\\d{2}")))
            val first = format.parse(start)!!
            val last = format.parse(endInclusive)!!
            require(!first.after(last))
            val calendar = Calendar.getInstance().apply { time = last; add(Calendar.DAY_OF_MONTH, 1) }
            val timestamp = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.US)
            timestamp.format(first) to timestamp.format(calendar.time)
        }.getOrNull()
    }

    fun monthRange(year: Int, month: Int): Pair<String, String> {
        val calendar = Calendar.getInstance().apply {
            clear(); set(year, month - 1, 1)
        }
        val day = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val first = day.format(calendar.time)
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        return requireNotNull(dateRange(first, day.format(calendar.time)))
    }

    fun timestampMillis(timestamp: String): Long {
        val normalized = timestamp.replace(" ", "T").replace(Regex("\\.\\d+(?=[Z+-])"), "")
        return runCatching {
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.US).parse(normalized)
        }.getOrNull()?.time ?: runCatching {
            SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).parse(timestamp)?.time
        }.getOrNull() ?: 0L
    }

    fun localDate(timestamp: String): String =
        SimpleDateFormat("yyyy-MM-dd", Locale.US).format(java.util.Date(timestampMillis(timestamp)))

    fun localTime(timestamp: String): String =
        SimpleDateFormat("HH:mm", Locale.US).format(java.util.Date(timestampMillis(timestamp)))

    fun budgetThreshold(spent: Double, limit: Double): Int = when {
        limit <= 0 -> 0
        spent >= limit -> 100
        spent >= limit * 0.8 -> 80
        else -> 0
    }
}
