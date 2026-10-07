package com.kashuapp.data.currency

import com.kashuapp.data.KashuSupaBase


class CurrencyRepository : ICurrencyRepository {

    override suspend fun getAllCurrency(): Result<List<Currency>> {
        try {
            val accTypeTable =
                KashuSupaBase.db.from("currency").select().decodeList<Currency>()
            return Result.success(accTypeTable)

        } catch (e: Exception) {
            return Result.failure(e)

        }
    }

}