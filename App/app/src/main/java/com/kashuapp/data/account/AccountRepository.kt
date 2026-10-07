package com.kashuapp.data.account

import com.kashuapp.data.KashuSupaBase

class AccountRepository : IAccountRepository {
    override suspend fun getAllAccount(userId: String): Result<List<Account>> {
        try {
            val accTable = KashuSupaBase.db.from("account").select().decodeList<Account>()
            val filtered = accTable.filter { account ->
                account.userId == null || account.userId == userId
            }
            return Result.success(filtered)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }
    override suspend fun deleteAccount(accountId: String): Result<Unit> {
        return try {
            KashuSupaBase.db.from("account").delete {
                filter {
                    eq("id", accountId)
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createAccount(account: Account): Result<Account> {
        return try {
            val response = KashuSupaBase.db.from("account")
                .insert(account) {
                    select()
                }
                .decodeSingle<Account>()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    override suspend fun updateAccount(account: Account): Result<Unit> {
        return try {
            KashuSupaBase.db.from("account").update(account) {
                filter {
                    eq("id", account.id ?: "")
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}