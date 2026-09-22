package com.kashuapp.data.transaction

import com.kashuapp.data.KashuSupaBase
import io.github.jan.supabase.postgrest.result.PostgrestResult


class TransactionRepository : ITransactionRepository {


    override suspend fun getAll(): Result<List<Transaction>> {

        try {


            val tableTrans = KashuSupaBase.db.from("transaction").select().decodeList<Transaction>()



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


