package com.kashuapp.data.transaction

import com.kashuapp.data.KashuSupaBase


class TransactionRepository : ITransactionRepository {


    override suspend fun getAll(userId: String, accountId: String?): Result<List<Transaction>> {
        try {
            val tableTrans = KashuSupaBase.db.from("transaction").select {
                filter {
                    if (!accountId.isNullOrBlank()) {
                        eq("account_id", accountId)
                    }
                }
            }.decodeList<Transaction>()
            return Result.success(tableTrans)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }


    override suspend fun postTrans(

        trans: Transaction

    ): Result<Unit> {

        try {

            KashuSupaBase.db.from("transaction").insert(
                (trans)
            )
            return Result.success(Unit)


        } catch (e: Exception) {

            return Result.failure(e)
        }


    }


}


