package com.kashuapp.data.accounts

import com.kashuapp.data.KashuSupaBase

class AccountRepository : IAccountRepository {

    override suspend fun getAllAccount(): Result<List<Account>> {

        try {

            val accountTable = KashuSupaBase.db.from("account").select().decodeList<Account>()
            return Result.success(accountTable)

        } catch (e: Exception) {
            return Result.failure(e)
        }

    }

}