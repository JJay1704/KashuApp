package com.kashuapp.data.transaction

import com.kashuapp.data.KashuSupaBase
import kotlin.Result


class TransactionRepository : ITransactionRepository {


    override suspend fun deleteTrans(transId: String): Result<Unit> {


        return try {
            KashuSupaBase.db.from("transaction").delete {
                filter {
                    eq("id", transId)
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateTrans(trans: Transaction): Result<Unit> {
        return try {


            KashuSupaBase.db.from("transaction").update(trans) {

                filter {
                    eq("id", trans.id ?: "")
                }

            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }


    }


    override suspend fun getAll(
        userId: String ,
        accountId: String? ,
        categoryId: String?,
        date: String?,
        startDate: String? ,
        endDate: String?
    ): Result<List<Transaction>> {
        try {
            val tableTrans = KashuSupaBase.db.from("transaction").select {
                filter {

                    if (!accountId.isNullOrBlank()) {
                        eq("account_id", accountId)
                    }
                    if (!categoryId.isNullOrBlank()) {
                        eq("category_id", categoryId)
                    }
                    if (!startDate.isNullOrBlank()) {
                        gte("transaction_date", startDate)
                    }
                    if (!endDate.isNullOrBlank()) {
                        lte("transaction_date", endDate)
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


