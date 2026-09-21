package com.kashuapp.data.accounts

import android.annotation.SuppressLint
import com.kashuapp.data.KashuSupaBase

class AccountRepository : IAccountRepository {


    @SuppressLint("SuspiciousIndentation")
    override suspend fun getAllAccount(): Result<List<Account>>{

        try {

            val accountTable = KashuSupaBase.db.from("account")
                .select().decodeList<Account>()
                return Result.success(accountTable)

        } catch (e: Exception) {
            return Result.failure(e)        }

    }

}