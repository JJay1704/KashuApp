package com.kashuapp.data.accounType

import com.kashuapp.data.KashuSupaBase

class AccountTypeRepository : IAccountTypeRepository {

    override suspend fun getAllAccountTypes(): Result<List<AccountType>> {
        try {
            val accTypeTable =
                KashuSupaBase.db.from("account_type").select().decodeList<AccountType>()
            return Result.success(accTypeTable)

        } catch (e: Exception) {
            return Result.failure(e)

        }
    }

}